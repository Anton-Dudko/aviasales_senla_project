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


    public Ticket save(TicketRequest request) {
        return Optional.ofNullable(request)
                .map(ticketMapper::convertDtoToEntity)
                .map(ticketRepository::save)
                .orElse(null);
    }

    public List<TicketResponse> search(TicketRequest request) {
        return Optional.ofNullable(request)
                .map(req -> criteriaTicketService.findAll(request.getUserId(), request.getStatus(), request.getTripId()))
                .map(ticketMapper::convertListEntityToDto)
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
        Optional.ofNullable(id)
                .map(ticketRepository::findTicketById)
                .map(t -> {
                    t.setUserId(userId);
                    t.setStatus(TicketStatus.BOOKED);
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


}


