package com.aviasalestickets.controller;

import com.aviasalestickets.model.dto.BookingRequest;
import com.aviasalestickets.model.dto.GenerateTicketRequest;
import com.aviasalestickets.model.dto.TicketRequest;
import com.aviasalestickets.model.dto.TicketResponse;
import com.aviasalestickets.service.TicketService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
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

    @GetMapping("/search")
    public List<TicketResponse> search(@RequestBody TicketRequest request) {
            return ticketService.search(request);
    }

    @GetMapping("/{id}")
    public TicketResponse findById(@PathVariable Long id) {
        return ticketService.findById(id);
    }

    @GetMapping("/findByTrip")
    public List<TicketResponse> findByTripId(@RequestParam("tripId") Long tripId) {
        return ticketService.findByTripId(tripId);
    }

    @GetMapping("/findByStatus")
    public List<TicketResponse> findByStatus(@RequestParam("status") String status) {
        return ticketService.findByStatus(status);
    }

    @PostMapping("/booking")
    public void bookingTicket(@RequestBody BookingRequest request) {
        ticketService.bookTicket(request.getId(), request.getUserId());
    }

    @DeleteMapping("/{id}")
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
