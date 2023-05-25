package com.aviasalestickets.repository;

import com.aviasalestickets.model.Ticket;
import com.aviasalestickets.model.TicketStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface TicketRepository extends JpaRepository<Ticket, Long>, JpaSpecificationExecutor<Ticket> {

    List<Ticket> findByStatus(TicketStatus status);

    List<Ticket> findByTripId(Long tripId);
}
