package com.aviasalestickets.service;

import com.aviasalestickets.mapper.TicketMapper;
import com.aviasalestickets.model.Ticket;
import com.aviasalestickets.model.TicketStatus;
import com.aviasalestickets.model.dto.*;
import com.aviasalestickets.model.dto.trip.FlightInfoDto;
import com.aviasalestickets.model.dto.user.UserResponse;
import com.aviasalestickets.repository.TicketRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class TicketService {

    private final TicketMapper ticketMapper;
    private final TicketRepository ticketRepository;
    private final List<GenerateTicketService> generateTicketServices;
    private final CriteriaTicketService criteriaTicketService;
    private final RestTemplate restTemplate;

    public static final String NEW_TICKET_RESERVATION_EVENT = "new_ticket_reservation_event";
    public static final String CANCELED_TICKET_RESERVATION_EVENT = "canceled_ticket_reservation_event";

    private final Producer<String, Map<String, KafkaTicketDto>> producer;
    private final ObjectMapper objectMapper;

    public Ticket save(TicketRequest request) {
        return Optional.ofNullable(request)
                .map(ticketMapper::convertDtoToEntity)
                .map(ticketRepository::save)
                .orElse(null);
    }

    public TicketResponseWithCount search(TicketRequest request) {
        log.info(request.toString());
        return Optional.ofNullable(request)
                .map(req -> criteriaTicketService.findAll(request.getUserId(), request.getStatus(), request.getTripId(), request.getFio(), request.getId()))
                .map(ticketMapper::convertListEntityToDtoWithCount)
                .orElse(null);
    }

    public TicketResponse findById(Long id) {
        return Optional.ofNullable(id)
                .map(ticketRepository::findTicketById)
                .map(ticketMapper::convertEntityToDto)
                .orElse(null);
    }
    //TODO exceptions

    public void bookTicket(Long id, Long userId) {

        Ticket newTicket = ticketRepository.findTicketById(id);

        FlightInfoDto flightInfoDto = requestToTrip(newTicket.getTripId());

        KafkaTicketDto ticket = new KafkaTicketDto();
        ticket.setUserLanguage("RU");
        ticket.setEmail("antondudko01@gmail.com");
        ticket.setUserName("Anton");
        ticket.setFrom(flightInfoDto.getTrip().getDepartureCity());
        ticket.setTo(flightInfoDto.getTrip().getArrivalCity());
        ticket.setPrice("200");
        ticket.setTicketDate("2023");
        log.info("event ... {}", ticket);
        ProducerRecord<String, Map<String, KafkaTicketDto>> producerRecord =
                new ProducerRecord<>(NEW_TICKET_RESERVATION_EVENT, objectMapper.convertValue(ticket, Map.class));
        log.info("producerRecord ... {}", producerRecord);
        producer.send(producerRecord);
        log.info("Sending message ... {}", producerRecord);
        Optional.ofNullable(id)
                .map(ticketRepository::findTicketById)
                .map(t -> {
                    t.setUserId(userId);
                    t.setStatus(TicketStatus.PAID);
                    return ticketRepository.save(t);
                })
                .orElseThrow(RuntimeException::new);
        //TODO add exception
    }

    public void deleteReservation(Long id) {
        Ticket ticket = ticketRepository.findTicketById(id);
        ticket.setStatus(TicketStatus.FREE);
        ticket.setUserId(null);
        ticketRepository.save(ticket);

        FlightInfoDto flightInfoDto = requestToTrip(ticket.getTripId());

        KafkaTicketDto kafkaTicketDto =new KafkaTicketDto();
        kafkaTicketDto.setUserLanguage("RU");
        kafkaTicketDto.setEmail("antondudko01@gmail.com");
        kafkaTicketDto.setUserName("Anton");
        kafkaTicketDto.setFrom(flightInfoDto.getTrip().getDepartureCity());
        kafkaTicketDto.setTo(flightInfoDto.getTrip().getArrivalCity());
        kafkaTicketDto.setPrice("200");
        kafkaTicketDto.setTicketDate("2023");

        log.info("info" + kafkaTicketDto);

                ProducerRecord<String, Map<String, KafkaTicketDto>> producerRecord =
                new ProducerRecord<>(CANCELED_TICKET_RESERVATION_EVENT, objectMapper.convertValue(ticket, Map.class));
        log.info("producerRecord ... {}", producerRecord);
        producer.send(producerRecord);
        log.info("Sending message ... {}", producerRecord);

    }

    public void payTicket(Long id) {
        Ticket ticket = ticketRepository.findTicketById(id);
        ticket.setStatus(TicketStatus.PAID);
        ticketRepository.save(ticket);
    }

    public void generateTickets(GenerateTicketRequest request) {
        List<Ticket> ticketsToSave = new ArrayList<>();
        for (GenerateTicketService generateTicketService : generateTicketServices) {
            ticketsToSave.addAll(generateTicketService.generate(request));
        }
        ticketRepository.saveAll(ticketsToSave);

    }

    public ResponseEntity<?> payTickets(List<Long> ticketsId) {
        List<Ticket> tickets = ticketRepository.findAllById(ticketsId);
        log.info(tickets.toString());

        for(Ticket ticket : tickets){
            if (!ticket.getStatus().equals(TicketStatus.FREE)){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Tickets not FREE or not exist!");
            }
        }

        for(Ticket ticket : tickets){
            ticket.setStatus(TicketStatus.PAID);
            ticketRepository.save(ticket);
        }
        return ResponseEntity.status(HttpStatus.OK).body("Tickets number - " + ticketsId + " paid!");

    }

    public FlightInfoDto requestToTrip(Long id){
        String url = "http://localhost:3000/flights/admin/find/{id}"; //tripservice:8080
        FlightInfoDto flight = restTemplate.getForObject(url, FlightInfoDto.class, id);
        return flight;
    }

    public List<Ticket> findAllByIds(List<Long> ids) {
        return ticketRepository.findAllById(ids);
    }

    public UserResponse requestToUser(Long id){
        String url = "http://userservice:8086/flights/admin/find/{id}";
        UserResponse user = restTemplate.getForObject(url, UserResponse.class, id);
        return user;
    }
}


