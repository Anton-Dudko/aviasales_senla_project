package com.aviasalestickets.repository;

import com.aviasalestickets.model.Ticket;
import com.aviasalestickets.model.TicketStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TicketRepository extends JpaRepository<Ticket, Long> {

    List<Ticket> findByStatus(TicketStatus status);
}
