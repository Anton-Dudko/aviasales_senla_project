package com.aviasalestickets.controller;

import com.aviasalestickets.model.Ticket;
import com.aviasalestickets.model.dto.request.BookingRequest;
import com.aviasalestickets.model.dto.request.GenerateTicketRequest;
import com.aviasalestickets.model.dto.request.TicketRequest;
import com.aviasalestickets.model.dto.response.*;
import com.aviasalestickets.service.TicketService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;


@RestController
@RequiredArgsConstructor
@RequestMapping("/tickets")
@Slf4j
public class TicketController {

    private final TicketService ticketService;

    @PostMapping
    public Ticket createTicket(@Valid @RequestBody TicketRequest request) {
        return ticketService.save(request);
    }

    @GetMapping
    public TicketResponseWithCount search(TicketRequest request) {
        return ticketService.search(request);
    }

    @GetMapping("/{id}")
    public TicketResponse findById(@PathVariable Long id) {
        return ticketService.findById(id);
    }

    @GetMapping("/ids")
    public List<TicketResponse> findTicketsByIds(@RequestParam("ids") List<Long> ids,
                                                 @RequestHeader("userDetails") String userDetails) {
        return ticketService.findAllByIds(ids, userDetails);
    }

    @PostMapping("/booking")
    public BookTicketResponse bookingTicket(@Valid @RequestBody BookingRequest request,
                                            @NotEmpty @NotNull @RequestHeader("userDetails") String userDetails) {
        return ticketService.bookTicket(request.getId(), request.getUserId(), userDetails);
    }

    @GetMapping("/pay-tickets")
    public PayTicketResponse payTickets(@RequestParam List<Long> ticketsId,
                                        @RequestHeader("userDetails") String userDetails) {
        ticketService.payTickets(ticketsId, userDetails);
        return PayTicketResponse.builder()
                .ticketsId(ticketsId)
                .message("Tickets paid")
                .build();
    }

    @DeleteMapping("/{id}")
    public BookingDeleteResponse deleteReservation(@PathVariable Long id,
                                                   @RequestHeader("userDetails") String userDetails) {
        ticketService.deleteReservation(id, userDetails);
        return BookingDeleteResponse.builder()
                .message(String.format("Ticket reservation delete is success with id: %s", id))
                .id(id)
                .build();
    }

    @PostMapping("/refund")
    public RefundTicketResponse refundTickets(@RequestParam List<Long> ticketsId) {
        ticketService.refundTickets(ticketsId);
        return RefundTicketResponse.builder()
                .ticketsId(ticketsId)
                .message("Tickets refund")
                .build();
    }

    @PostMapping("/generate")
    public GenerateTicketResponse generateTickets(@Valid @RequestBody GenerateTicketRequest request) {
        ticketService.generateTickets(request);
        return GenerateTicketResponse.builder()
                .flightId(request.getFlightId())
                .message(String.format("Tickets generated for flight %s", request.getFlightId()))
                .build();
    }

}
