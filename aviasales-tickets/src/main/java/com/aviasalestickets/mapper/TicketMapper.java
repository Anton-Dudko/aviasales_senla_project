package com.aviasalestickets.mapper;

import com.aviasalestickets.model.Ticket;
import com.aviasalestickets.model.TicketStatus;
import com.aviasalestickets.model.TicketType;
import com.aviasalestickets.model.dto.kafka.KafkaTicketDto;
import com.aviasalestickets.model.dto.request.TicketRequest;
import com.aviasalestickets.model.dto.response.TicketResponse;
import com.aviasalestickets.model.dto.response.TicketResponseWithCount;
import com.aviasalestickets.model.dto.trip.FlightInfoDto;
import com.aviasalestickets.model.dto.user.UserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class TicketMapper {

    public Ticket convertDtoToEntity(TicketRequest request) {
        Ticket ticket = new Ticket();
        ticket.setFio(request.getFio());
        ticket.setPrice(request.getPrice());
        ticket.setUserId(request.getUserId());
        ticket.setFlightId(request.getFlightId());
        ticket.setType(TicketType.valueOf(request.getType()));
        ticket.setStatus(TicketStatus.valueOf(request.getStatus()));
        return ticket;
    }

    public TicketResponse convertEntityToDto(Ticket ticket) {
        TicketResponse ticketResponse = new TicketResponse();
        ticketResponse.setId(ticket.getId());
        ticketResponse.setUserId(ticket.getUserId());
        ticketResponse.setFlightId(ticket.getFlightId());
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

    public TicketResponseWithCount convertListEntityToDtoWithCount(List<Ticket> entityList) {
        TicketResponseWithCount ticketResponseWithCount = new TicketResponseWithCount(0, new ArrayList<>());

        for (Ticket t : entityList) {
            ticketResponseWithCount.getList().add(convertEntityToDto(t));
        }
        ticketResponseWithCount.setCount(ticketResponseWithCount.getList().size());
        return ticketResponseWithCount;
    }

    public Ticket buildTicket(BigDecimal price, int seatNumber, Long tripId, TicketType ticketType) {
        return Ticket.builder()
                .price(price)
                .seatNumber(seatNumber)
                .status(TicketStatus.FREE)
                .flightId(tripId)
                .type(ticketType)
                .build();
    }

    public KafkaTicketDto buildKafkaTicketDto(FlightInfoDto flightInfoDto, String price, UserDetails userDetails) {
        return KafkaTicketDto.builder()
                .userLanguage(userDetails.getLanguage())
                .email(userDetails.getEmail())
                .userName(userDetails.getUsername())
                .from(flightInfoDto.getTrip().getDepartureCity())
                .to(flightInfoDto.getTrip().getArrivalCity())
                .price(price)
                .ticketDate(LocalDate.now().toString())
                .build();
    }
}

