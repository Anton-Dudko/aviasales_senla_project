package com.aviasalestickets.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "ticket")
public class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private Long userId;

    private Long tripId;

    private String fio;

    @Enumerated(EnumType.STRING)
    private TicketType type;

    private BigDecimal price;

    @Enumerated(EnumType.STRING)
    private TicketStatus status;


}
