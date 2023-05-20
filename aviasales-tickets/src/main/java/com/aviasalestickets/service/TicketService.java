package com.aviasalestickets.service;

import com.aviasalestickets.mapper.TicketMapper;
import com.aviasalestickets.model.Ticket;
import com.aviasalestickets.model.TicketStatus;
import com.aviasalestickets.model.dto.TicketRequest;
import com.aviasalestickets.model.dto.TicketResponse;
import com.aviasalestickets.repository.TicketRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TicketService {

    private final TicketMapper ticketMapper;
    private final TicketRepository ticketRepository;

    public String save(TicketRequest request){
        Ticket ticket = ticketMapper.convertDtoToEntity(request);
        ticketRepository.save(ticket);
        return "ticket was save";
    }

    public TicketResponse findById(Long id){
        return ticketMapper.convertEntityToDto(ticketRepository.findById(id).get());
    }

    public List<TicketResponse> findByStatus(String status) {
        return ticketMapper.convertListEntityToDto(ticketRepository.findByStatus(TicketStatus.valueOf(status)));
    }

    public List<TicketResponse> findAll() {
        return ticketMapper.convertListEntityToDto(ticketRepository.findAll());
    }

    public String bookTicket(Long id, Long userId) {
        Ticket ticket = ticketRepository.findById(id).get();
        ticket.setUserId(userId);
        ticket.setStatus(TicketStatus.BOOKED);
        ticketRepository.save(ticket);
        return "ticket successfully booked";
    }

    public String deleteReservation(Long id) {
        Ticket ticket = ticketRepository.findById(id).get();
        ticket.setStatus(TicketStatus.FREE);
        ticket.setUserId(null);
        ticketRepository.save(ticket);
        return "ticket reservation successfully deleted";
    }

    public String payTicket(Long id) {
        Ticket ticket = ticketRepository.findById(id).get();
        ticket.setStatus(TicketStatus.PAID);
        ticketRepository.save(ticket);
        return "ticket successfully paid";
    }
    // вывод через критерии
   // public List<Ticket> findAll(){}
    // эндпоит генерирования билетов
 //   public void createTickets(Long tripId){}
    // бронирование билета
   // public void
    // удаление брони

}

//что должен прнимать и возвращать эндпоинт бронирования
//наверное нужно на вход бронирования принимать id юзера
// при отмене бронирования должен меняться статус и очищаться userId
//должна ли пропадать бронь спустя какоето время

