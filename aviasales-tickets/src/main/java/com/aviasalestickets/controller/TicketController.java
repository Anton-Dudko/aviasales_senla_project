package com.aviasalestickets.controller;

import com.aviasalestickets.model.Ticket;
import com.aviasalestickets.model.dto.request.BookingRequest;
import com.aviasalestickets.model.dto.request.GenerateTicketRequest;
import com.aviasalestickets.model.dto.request.TicketRequest;
import com.aviasalestickets.model.dto.response.*;
import com.aviasalestickets.service.TicketService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/tickets")
public class TicketController {

    private final TicketService ticketService;

    @PostMapping
    public Ticket createTicket(@RequestBody TicketRequest request) {
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
    public List<TicketResponse> findTicketsByIds(@RequestParam("ids") List<Long> ids, @RequestHeader("userDetails") String userDetails) {
        return ticketService.findAllByIds(ids, userDetails);
    }

    @PostMapping("/booking")
    public BookTicketResponse bookingTicket(@RequestBody BookingRequest request, @RequestHeader("userDetails") String userDetails) {
        return ticketService.bookTicket(request.getId(), request.getUserId(), userDetails);
    }

    @GetMapping("/pay-tickets")
    public PayTicketResponse payTickets(@RequestParam List<Long> ticketsId, @RequestHeader("userDetails") String userDetails) {
        ticketService.payTickets(ticketsId, userDetails);
        return PayTicketResponse.builder()
                .ticketsId(ticketsId)
                .message("Tickets paid")
                .build();
    }

    @DeleteMapping("/{id}")
    public BookingDeleteResponse deleteReservation(@PathVariable Long id, @RequestHeader("userDetails") String userDetails) {
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
    public GenerateTicketResponse generateTickets(@RequestBody GenerateTicketRequest request) {
        ticketService.generateTickets(request);
        return GenerateTicketResponse.builder()
                .flightId(request.getFlightId())
                .message(String.format("Tickets generated for flight %s", request.getFlightId()))
                .build();
    }

}
