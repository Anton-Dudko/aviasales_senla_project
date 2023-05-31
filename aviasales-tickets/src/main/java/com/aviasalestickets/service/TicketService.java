package com.aviasalestickets.service;

import com.aviasalestickets.mapper.TicketMapper;
import com.aviasalestickets.model.Ticket;
import com.aviasalestickets.model.TicketStatus;
import com.aviasalestickets.model.dto.*;
import com.aviasalestickets.repository.TicketRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.stereotype.Service;

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

    public static final String REGISTERED_EVENT = "new_ticket_reservation_event";
    public static final String RESET_PASSWORD_EVENT = "canceled_ticket_reservation_event";

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
                .map(req -> criteriaTicketService.findAll(request.getUserId(), request.getStatus(), request.getTripId()))
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
        KafkaTicketDto ticket =new KafkaTicketDto();
        ticket.setUserLanguage("RU");
        ticket.setEmail("antondudko01@gmail.com");
        ticket.setUserName("Anton");
        ticket.setFrom("Minsk");
        ticket.setTo("Moscov");
        ticket.setPrice("200");
        ticket.setTicketDate("2023");
        log.info("event ... {}", ticket);
        ProducerRecord<String, Map<String, KafkaTicketDto>> producerRecord =
                new ProducerRecord<>(REGISTERED_EVENT, objectMapper.convertValue(ticket, Map.class));
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


    public String payTickets(List<Long> ticketsId) {
        List<Ticket> tickets = ticketRepository.findAllById(ticketsId);

        for(Ticket ticket : tickets){
            if (!ticket.getStatus().equals(TicketStatus.FREE)){
                return "not available";
            }
        }

        for(Ticket ticket : tickets){
            ticket.setStatus(TicketStatus.PAID);
            ticketRepository.save(ticket);
        }
        return "tickets were paid";
    }
}


