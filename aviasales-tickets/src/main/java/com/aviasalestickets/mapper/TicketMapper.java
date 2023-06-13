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
        return Ticket.builder()
                .fio(request.getFio())
                .price(request.getPrice())
                .userId(request.getUserId())
                .flightId(request.getFlightId())
                .type(TicketType.valueOf(request.getType()))
                .status(TicketStatus.valueOf(request.getStatus()))
                .build();
    }

    public TicketResponse convertEntityToDto(Ticket ticket) {
        return TicketResponse.builder()
                .id(ticket.getId())
                .userId(ticket.getUserId())
                .flightId(ticket.getFlightId())
                .fio(ticket.getFio())
                .type(ticket.getType().toString())
                .price(ticket.getPrice())
                .status(ticket.getStatus().toString())
                .build();
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

