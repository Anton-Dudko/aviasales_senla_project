package com.aviasalestickets.controller;

import com.aviasalestickets.model.Ticket;
import com.aviasalestickets.model.dto.*;
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

    @PostMapping("/booking")
    public void bookingTicket(@RequestBody BookingRequest request) {
        ticketService.bookTicket(request.getId(), request.getUserId());
    }

    @GetMapping("/pay-tickets")
    public String bookTickets(@RequestParam List<Long> ticketsId) {
        return ticketService.payTickets(ticketsId);
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
