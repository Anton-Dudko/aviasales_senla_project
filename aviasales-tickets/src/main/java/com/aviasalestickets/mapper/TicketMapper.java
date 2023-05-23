package com.aviasalestickets.mapper;

import com.aviasalestickets.model.Ticket;
import com.aviasalestickets.model.TicketStatus;
import com.aviasalestickets.model.TicketType;
import com.aviasalestickets.model.dto.TicketRequest;
import com.aviasalestickets.model.dto.TicketResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class TicketMapper {

    private static final BigDecimal FIRST_CLASS_PRICE = BigDecimal.valueOf(200);
    private static final BigDecimal SECOND_CLASS_PRICE = BigDecimal.valueOf(100);

    public Ticket convertDtoToEntity(TicketRequest request) {
        Ticket ticket = new Ticket();
        ticket.setFio(request.getFio());
        ticket.setPrice(request.getPrice());
        ticket.setUserId(request.getUserId());
        ticket.setTripId(request.getTripId());
        ticket.setType(TicketType.valueOf(request.getType()));
        ticket.setStatus(TicketStatus.valueOf(request.getStatus()));
        return ticket;
    }

    public TicketResponse convertEntityToDto(Ticket ticket) {
        TicketResponse ticketResponse = new TicketResponse();
        ticketResponse.setId(ticket.getId());
        ticketResponse.setUserId(ticket.getUserId());
        ticketResponse.setTripId(ticket.getTripId());
        ticketResponse.setFio(ticket.getFio());
        ticketResponse.setType(ticket.getType().toString());
        ticketResponse.setPrice(ticket.getPrice());
        ticketResponse.setStatus(ticket.getStatus().toString());
        return ticketResponse;
    }

    public List<TicketResponse> convertListEntityToDto(List<Ticket> entityList) {
        List<TicketResponse> list = new ArrayList<>();
        for (Ticket t : entityList) {
            list.add(convertEntityToDto(t));
        }
        return list;
    }

    public Ticket buildTicket(BigDecimal price, int seatNumber, Long tripId, TicketType ticketType) {
        return Ticket.builder()
                .price(price)
                .seatNumber(seatNumber)
                .status(TicketStatus.FREE)
                .tripId(tripId)
                .type(ticketType)
                .build();
    }
}

