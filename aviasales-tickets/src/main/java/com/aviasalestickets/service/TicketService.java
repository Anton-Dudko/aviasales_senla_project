package com.aviasalestickets.service;

import com.aviasalestickets.config.KafkaTopics;
import com.aviasalestickets.exception.*;
import com.aviasalestickets.mapper.TicketMapper;
import com.aviasalestickets.mapper.UserMapper;
import com.aviasalestickets.model.Ticket;
import com.aviasalestickets.model.TicketStatus;
import com.aviasalestickets.model.dto.request.GenerateTicketRequest;
import com.aviasalestickets.model.dto.request.TicketRequest;
import com.aviasalestickets.model.dto.response.BookTicketResponse;
import com.aviasalestickets.model.dto.response.TicketResponse;
import com.aviasalestickets.model.dto.response.TicketResponseWithCount;
import com.aviasalestickets.model.dto.user.UserDetails;
import com.aviasalestickets.repository.TicketRepository;
import com.aviasalestickets.service.api.TripApi;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
                .orElseThrow(() -> new TicketNotCreatedException(String.format("Ticket %s not create", request)));
    }

    public TicketResponseWithCount search(TicketRequest request) {
        return Optional.ofNullable(request)
                .map(req -> criteriaTicketService.findAll(request.getUserId(),
                        request.getStatus(),
                        request.getFlightId(),
                        request.getFio(),
                        request.getId(),
                        request.getType()))
                .map(ticketMapper::convertListEntityToDtoWithCount)
                .orElseThrow(() -> new TicketNotFoundException("Tickets with these parameters not found"));
    }

    public TicketResponse findById(Long id) {
        return Optional.ofNullable(id)
                .map(ticketRepository::findTicketById)
                .map(ticketMapper::convertEntityToDto)
                .orElseThrow(() -> new TicketNotFoundException(String.format("Ticket %s not found", id)));
    }

    @Transactional
    public BookTicketResponse bookTicket(Long id, Long userId, String userDetails) {
        UserDetails user = userMapper.getUserDetails(userDetails);
        Boolean validate = Optional.ofNullable(user)
                .map(userDetail -> StringUtils.isNotEmpty(userDetail.getEmail()))//TODO validation UTIL class valid all params
                .orElse(false);
        if (!validate) {
            throw new RuntimeException("User details is invalid");
        }

        return Optional.ofNullable(id)
                .map(ticketRepository::findTicketById)
                .filter(ticket -> ticket.getStatus().equals(TicketStatus.FREE))
                .map(t -> {
                    t.setUserId(userId);
                    t.setFio(user.getUsername());
                    t.setStatus(TicketStatus.BOOKED);
                    return ticketRepository.save(t);
                })
                .map(ticket -> {
                    var flightInfoDto = tripApi.requestToTrip(ticket.getFlightId());
                    return ticketMapper.buildKafkaTicketDto(flightInfoDto, ticket.getPrice().toString(), user);
                })
                .map(kafkaTicketDto -> kafkaProducerService.sendMessage(kafkaTicketDto,
                        KafkaTopics.NEW_TICKET_RESERVATION_EVENT))
                .map(kafkaTicketDto -> new BookTicketResponse(id))
                .orElseThrow(() -> new TicketNotBookedException(String.format("Ticket %s not booked", id)));
    }

    public void deleteReservation(Long ticketId, String userDetails) {
        UserDetails user = userMapper.getUserDetails(userDetails);
        Optional.ofNullable(ticketId)
                .map(ticketRepository::findTicketById)
                .filter(ticket -> ticket.getStatus().equals(TicketStatus.BOOKED))
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
                .map(kafkaTicketDto -> kafkaProducerService.sendMessage(kafkaTicketDto,
                        KafkaTopics.CANCELED_TICKET_RESERVATION_EVENT))
                .map(kafkaTicketDto -> new BookTicketResponse(ticketId))
                .orElseThrow(() ->
                        new TicketNotDeletedReservationException(String.format("Ticket %s not deleted reservation", ticketId)));
    }

    public void generateTickets(GenerateTicketRequest request) {
        List<Ticket> ticketsToSave = generateTicketServices.stream()
                .flatMap(service -> service.generate(request).stream())
                .collect(Collectors.toList());
        ticketRepository.saveAll(ticketsToSave);
    }

    public void payTickets(List<Long> ticketsId, String userDetails) {
        UserDetails user = userMapper.getUserDetails(userDetails);
        var validateTicket = ticketRepository.findAllById(ticketsId).stream()
                .filter(ticket -> ticket.getStatus().equals(TicketStatus.FREE) ||
                        ticket.getStatus().equals(TicketStatus.BOOKED))
                .peek(ticket -> {
                    ticket.setStatus(TicketStatus.PAID);
                    ticket.setUserId(user.getUserId());
                    ticket.setFio(user.getUsername());
                })
                .toList();
        if (validateTicket.size() != ticketsId.size()) {
            throw new TicketNotPaidException("Tickets cannot be paid");
        }
        ticketRepository.saveAll(validateTicket);
    }

    public void refundTickets(List<Long> ticketsId) {
        var validateTickets = ticketRepository.findAllById(ticketsId).stream()
                .peek(ticket -> {
                    ticket.setStatus(TicketStatus.FREE);
                    ticket.setFio(null);
                    ticket.setUserId(null);
                })
                .toList();
        if (validateTickets.size() != ticketsId.size()) {
            throw new TicketNotRefundException("Tickets cannot be refund");
        }
        ticketRepository.saveAll(validateTickets);
    }

    public List<TicketResponse> findAllByIds(List<Long> ids) {
        List<Ticket> tickets = ticketRepository.findAllById(ids);
        return tickets
                .stream()
                .map(ticketMapper::convertEntityToDto)
                .collect(Collectors.toList());
    }
}


