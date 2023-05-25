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
public class GenerateFirstClassTicketService implements GenerateTicketService {

    private static final BigDecimal FIRST_CLASS_PRICE = BigDecimal.valueOf(200);

    private final TicketMapper ticketMapper;

    @Override
    public List<Ticket> generate(GenerateTicketRequest request) {
        List<Ticket> list = new ArrayList<>();
        for (int i = 1; i < request.getCountTicketsFirstClass() + 1; i++) {
            Ticket ticket = ticketMapper.buildTicket(FIRST_CLASS_PRICE, i, request.getTripId(), TicketType.FIRST_CLASS);
            list.add(ticket);
        }
        return list;
    }
}
