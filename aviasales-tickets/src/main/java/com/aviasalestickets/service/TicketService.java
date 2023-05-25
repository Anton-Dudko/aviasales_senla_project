package com.aviasalestickets.service;

import com.aviasalestickets.mapper.TicketMapper;
import com.aviasalestickets.model.Ticket;
import com.aviasalestickets.model.TicketStatus;
import com.aviasalestickets.model.dto.GenerateTicketRequest;
import com.aviasalestickets.model.dto.TicketRequest;
import com.aviasalestickets.model.dto.TicketResponse;
import com.aviasalestickets.repository.TicketRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TicketService {

    private final TicketMapper ticketMapper;
    private final TicketRepository ticketRepository;
    private final List<GenerateTicketService> generateTicketServices;
    private final CriteriaTicketService criteriaTicketService;


    public String save(TicketRequest request) {
        Ticket ticket = ticketMapper.convertDtoToEntity(request);
        ticketRepository.save(ticket);
        return "ticket was save";
    }

    public List<TicketResponse> search(TicketRequest request) {
        return Optional.ofNullable(request)
                .map(req-> criteriaTicketService.findAll(request.getUserId(), request.getStatus(), request.getTripId()))
                .map(ticketMapper::convertListEntityToDto)
                .orElse(null);
    }

    public TicketResponse findById(Long id) {
//        return Optional.ofNullable(id)
//                .map(ticketRepository::findById)
//                .map(ticketMapper::convertEntityToDto)
//                .orElse(null);
        return ticketMapper.convertEntityToDto(ticketRepository.findById(id).get());
    }

    public List<TicketResponse> findByStatus(String status) {
        return Optional.of(status)
                .map(st -> ticketRepository.findByStatus(TicketStatus.valueOf(st)))
                .map(ticketMapper::convertListEntityToDto)
                .orElse(null);
    }

    public List<TicketResponse> findByTripId(Long tripId) {
        return Optional.of(tripId)
                .map(ticketRepository::findByTripId)
                .map(ticketMapper::convertListEntityToDto)
                .orElse(null);
    }

    public List<TicketResponse> findAll() {
        return ticketMapper.convertListEntityToDto(ticketRepository.findAll());
    }

    public void bookTicket(Long id, Long userId) {
        Ticket ticket = ticketRepository.findById(id).get();
        ticket.setUserId(userId);
        ticket.setStatus(TicketStatus.BOOKED);
        ticketRepository.save(ticket);
    }

    public void deleteReservation(Long id) {
        Ticket ticket = ticketRepository.findById(id).get();
        ticket.setStatus(TicketStatus.FREE);
        ticket.setUserId(null);
        ticketRepository.save(ticket);
    }

    public void payTicket(Long id) {
        Ticket ticket = ticketRepository.findById(id).get();
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


}


