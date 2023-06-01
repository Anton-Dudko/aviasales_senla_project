package com.aviasalestickets.service;

import com.aviasalestickets.mapper.TicketMapper;
import com.aviasalestickets.model.Ticket;
import com.aviasalestickets.model.TicketType;
import com.aviasalestickets.model.dto.GenerateTicketRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GenerateSecondClassTicketService implements GenerateTicketService {

    private final TicketMapper ticketMapper;

    @Override
    public List<Ticket> generate(GenerateTicketRequest request) {
        List<Ticket> list = new ArrayList<>();
        int count = request.getCountTicketsFirstClass() + request.getCountTicketsSecondClass();
        for (int i = request.getCountTicketsFirstClass() + 1; i < count + 1; i++) {
            Ticket ticket = ticketMapper.buildTicket(BigDecimal.valueOf(request.getSecondClassPrice()), i, request.getTripId(), TicketType.SECOND_CLASS);
            list.add(ticket);
        }
        return list;
    }
}
