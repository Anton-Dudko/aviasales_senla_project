package com.aviasalestickets.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "ticket")
public class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private Long userId;

    private Long tripId;

    private String fio;

    private Integer seatNumber;

    @Enumerated(EnumType.STRING)
    private TicketType type;

    private BigDecimal price;

    @Enumerated(EnumType.STRING)
    private TicketStatus status;


}
