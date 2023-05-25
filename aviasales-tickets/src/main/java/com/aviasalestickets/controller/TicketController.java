package com.aviasalestickets.controller;

import com.aviasalestickets.model.dto.BookingRequest;
import com.aviasalestickets.model.dto.GenerateTicketRequest;
import com.aviasalestickets.model.dto.TicketRequest;
import com.aviasalestickets.model.dto.TicketResponse;
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
    public String createTicket(@RequestBody TicketRequest request) {
        return ticketService.save(request);
    }

    @GetMapping
    public List<TicketResponse> findAll() {
        return ticketService.findAll();
    }

    @GetMapping("/{id}")
    public TicketResponse findById(@PathVariable Long id) {
        return ticketService.findById(id);
    }

    @GetMapping("/findByTripId/{tripId}")
    public List<TicketResponse> findByTripId(@PathVariable Long tripId) {
        return ticketService.findByTripId(tripId);
    }

    @GetMapping("/findByStatus/{status}")
    public List<TicketResponse> findByStatus(@PathVariable String status) {
        return ticketService.findByStatus(status);
    }

    @PostMapping("/booking")
    public void bookingTicket(@RequestBody BookingRequest request) {
        ticketService.bookTicket(request.getId(), request.getUserId());
    }

    @PostMapping("/deleteBook/{id}")
    public void deleteReservation(@PathVariable Long id) {
        ticketService.deleteReservation(id);
    }

    @PostMapping("/payTicket/{id}")
    public void payTicket(@PathVariable Long id) {
        ticketService.payTicket(id);
    }

    @PostMapping("/generate")
    public void generateTickets(@RequestBody GenerateTicketRequest request) {
        ticketService.generateTickets(request);
    }

}
