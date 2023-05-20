package com.aviasalestickets.controller;

import com.aviasalestickets.model.TicketStatus;
import com.aviasalestickets.model.dto.BookingRequest;
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
    public List<TicketResponse> findAll(){
        return ticketService.findAll();
    }

    @GetMapping("/{id}")
    public TicketResponse findById(@PathVariable Long id){
        return ticketService.findById(id);
    }

    @GetMapping("/findByStatus/{status}")
    public List<TicketResponse> findByStatus(@PathVariable String status){
        return ticketService.findByStatus(status);
    }

    @PostMapping("/booking")
    public String bookingTicket(@RequestBody BookingRequest request){
        return ticketService.bookTicket(request.getId(), request.getUserId());
    }

    @PostMapping("/deleteBook/{id}")
    public String deleteReservation(@PathVariable Long id){
        return ticketService.deleteReservation(id);
    }

    @PostMapping("/payTicket/{id}")
    public String payTicket(@PathVariable Long id){
            return ticketService.payTicket(id);
    }

}
