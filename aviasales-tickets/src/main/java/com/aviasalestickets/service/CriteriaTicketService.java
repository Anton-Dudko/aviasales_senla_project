package com.aviasalestickets.service;

import com.aviasalestickets.model.Ticket;
import com.aviasalestickets.repository.TicketRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

import static org.springframework.data.jpa.domain.Specification.where;

@Service
@RequiredArgsConstructor
public class CriteriaTicketService {

    private final TicketRepository ticketRepository;

    public List<Ticket> findAll(Long userId, String status, Long tripId, String fio, Long id, String type) {
        return ticketRepository.findAll(
                where(TicketSpecifications.userIdEqual(userId)
                        .and(TicketSpecifications.statusEqual(status))
                        .and(TicketSpecifications.flightIdEqual(tripId))
                        .and(TicketSpecifications.ticketIdEqual(id))
                        .and(TicketSpecifications.ticketFioEqual(fio))
                        .and(TicketSpecifications.ticketTypeEqual(type))));
    }
}
