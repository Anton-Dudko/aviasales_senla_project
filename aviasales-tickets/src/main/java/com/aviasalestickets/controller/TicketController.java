package com.aviasalestickets.controller;

import com.aviasalestickets.model.Ticket;
import com.aviasalestickets.model.dto.*;
import com.aviasalestickets.service.TicketService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    @GetMapping("/findByIds")
    public List<Ticket> findTicketsByIds(@RequestParam("ids") List<Long> ids) {
        return ticketService.findAllByIds(ids);
    }

    @PostMapping("/booking")
    public BookTicketResponse bookingTicket(@RequestBody BookingRequest request, @RequestHeader("userDetails") String userDetails) {
        log.info(userDetails.toString());
        return ticketService.bookTicket(request.getId(), request.getUserId(), userDetails);
    }

    @GetMapping("/pay-tickets")
    public ResponseEntity<?> payTickets(@RequestParam List<Long> ticketsId) {
        return ticketService.payTickets(ticketsId);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteReservation(@PathVariable Long id, @RequestHeader("userDetails") String userDetails) {
        ticketService.deleteReservation(id, userDetails);
        return ResponseEntity.status(HttpStatus.OK).body("Ticket number - " + id + " deleted!");
    }

    @PostMapping("/pay/{id}")
    public ResponseEntity<?> payTicket(@PathVariable Long id) {
        ticketService.payTicket(id);
        return ResponseEntity.status(HttpStatus.OK).body("Ticket number - " + id + " paid!");
    }

    @PostMapping("/refund")
    public ResponseEntity<?> refundTickets(@RequestParam List<Long> ticketsId) {
        return ticketService.refundTickets(ticketsId);
    }

    @PostMapping("/generate")
    public ResponseEntity<?> generateTickets(@RequestBody GenerateTicketRequest request) {
        ticketService.generateTickets(request);
        return ResponseEntity.status(HttpStatus.OK).body("Tickets for trip number - " + request.getFlightId() + " created");
    }

}
