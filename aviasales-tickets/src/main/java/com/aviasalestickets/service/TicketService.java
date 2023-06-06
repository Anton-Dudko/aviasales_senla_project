package com.aviasalestickets.service;

import com.aviasalestickets.config.KafkaTopics;
import com.aviasalestickets.exception.*;
import com.aviasalestickets.mapper.TicketMapper;
import com.aviasalestickets.mapper.UserMapper;
import com.aviasalestickets.model.Ticket;
import com.aviasalestickets.model.TicketStatus;
import com.aviasalestickets.model.dto.*;
import com.aviasalestickets.model.dto.user.UserDetails;
import com.aviasalestickets.repository.TicketRepository;
import com.aviasalestickets.service.api.TripApi;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class TicketService {

    private final TicketMapper ticketMapper;
    private final TicketRepository ticketRepository;
    private final List<GenerateTicketService> generateTicketServices;
    private final CriteriaTicketService criteriaTicketService;
    private final TripApi tripApi;
    private final KafkaProducerService kafkaProducerService;
    private final UserMapper userMapper;


    public Ticket save(TicketRequest request) {
        return Optional.ofNullable(request)
                .map(ticketMapper::convertDtoToEntity)
                .map(ticketRepository::save)
                .orElseThrow(() -> new TicketNotCreatedException("Ticket " + request.getId() + "not create"));
    }

    public TicketResponseWithCount search(TicketRequest request) {
        return Optional.ofNullable(request)
                .map(req -> criteriaTicketService.findAll(request.getUserId(), request.getStatus(), request.getFlightId(), request.getFio(), request.getId()))
                .map(ticketMapper::convertListEntityToDtoWithCount)
                .orElseThrow(() -> new TicketNotFoundException("Tickets with these parameters not found"));
    }

    public TicketResponse findById(Long id) {
        return Optional.ofNullable(id)
                .map(ticketRepository::findTicketById)
                .map(ticketMapper::convertEntityToDto)
                .orElseThrow(() -> new TicketNotFoundException("Ticket " + id + " not found"));
    }

    public BookTicketResponse bookTicket(Long id, Long userId, String userDetails) {
        UserDetails user = userMapper.getUserDetails(userDetails);
        log.info(user.toString());
        return Optional.ofNullable(id)
                .map(ticketRepository::findTicketById)
                .map(t -> {
                    t.setUserId(userId);
                    t.setStatus(TicketStatus.BOOKED);
                    return ticketRepository.save(t);
                })
                .map(ticket -> {
                    var flightInfoDto = tripApi.requestToTrip(ticket.getFlightId());
                    return ticketMapper.buildKafkaTicketDto(flightInfoDto, ticket.getPrice().toString(), user);
                })
                .map(kafkaTicketDto -> kafkaProducerService.sendMessage(kafkaTicketDto, KafkaTopics.NEW_TICKET_RESERVATION_EVENT))
                .map(kafkaTicketDto -> new BookTicketResponse(id))
                .orElseThrow(() -> new TicketNotBookedException("Ticket " + id + "not booked"));
    }

    public void deleteReservation(Long id, String userDetails) {
        UserDetails user = userMapper.getUserDetails(userDetails);
        log.info(user.toString());

        Optional.ofNullable(id)
                .map(ticketRepository::findTicketById)
                .map(ticket -> {
                    ticket.setStatus(TicketStatus.FREE);
                    ticket.setUserId(null);
                    ticket.setFio(null);
                    return ticketRepository.save(ticket);
                })
                .map(ticket -> {
                    var flightInfoDto = tripApi.requestToTrip(ticket.getFlightId());
                    return ticketMapper.buildKafkaTicketDto(flightInfoDto, ticket.getPrice().toString(), user);
                })
                .map(kafkaTicketDto -> kafkaProducerService.sendMessage(kafkaTicketDto, KafkaTopics.CANCELED_TICKET_RESERVATION_EVENT))
                .map(kafkaTicketDto -> new BookTicketResponse(id))
                .orElseThrow(() -> new TicketNotDeletedReservationException("Ticket " + id + "not deleted reservation"));
    }

    public void payTicket(Long id) {
        Optional.ofNullable(id)
                .map(ticketRepository::findTicketById)
                .map(ticket -> {
                    ticket.setStatus(TicketStatus.PAID);
                    return ticketRepository.save(ticket);
                })
                .orElseThrow(() -> new TicketNotPaidException("Ticket " + id + "not paid"));
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

        for (Ticket ticket : tickets) {
            if (!ticket.getStatus().equals(TicketStatus.FREE)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Tickets not FREE or not exist!");
            }
        }

        for (Ticket ticket : tickets) {
            ticket.setStatus(TicketStatus.PAID);
            ticketRepository.save(ticket);
        }
        return ResponseEntity.status(HttpStatus.OK).body("Tickets number - " + ticketsId + " paid!");
    }

    public ResponseEntity<?> refundTickets(List<Long> ticketsId) {
        List<Ticket> tickets = ticketRepository.findAllById(ticketsId);

        for (Ticket ticket : tickets) {
            ticket.setStatus(TicketStatus.FREE);
            ticketRepository.save(ticket);
        }
        return ResponseEntity.status(HttpStatus.OK).body("Tickets number - " + ticketsId + " refunded!");
    }

    public List<Ticket> findAllByIds(List<Long> ids) {
        return ticketRepository.findAllById(ids);
    }
}


