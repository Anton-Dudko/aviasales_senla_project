package com.aviasalestickets.controller;

import com.aviasalestickets.model.Ticket;
import com.aviasalestickets.model.dto.*;
import com.aviasalestickets.service.TicketService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @PostMapping("/booking")
    public void bookingTicket(@RequestBody BookingRequest request) {
        ticketService.bookTicket(request.getId(), request.getUserId());
    }

    @DeleteMapping("/{id}")
    public void deleteReservation(@PathVariable Long id) {
        ticketService.deleteReservation(id);
    }

    @PostMapping("/pay/{id}")
    public void payTicket(@PathVariable Long id) {
        ticketService.payTicket(id);
    }

    @PostMapping("/generate")
    public void generateTickets(@RequestBody GenerateTicketRequest request) {
        ticketService.generateTickets(request);
    }

}
