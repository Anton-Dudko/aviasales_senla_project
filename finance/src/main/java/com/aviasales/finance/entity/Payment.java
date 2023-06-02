package com.aviasales.finance.entity;

import com.aviasales.finance.enums.PaymentStatus;
import lombok.*;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "payments")
@Getter
@Setter
@ToString
@RequiredArgsConstructor
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "amount")
    private BigDecimal amount;
    private LocalDateTime paymentCreationDateTime;

    @ElementCollection
    @CollectionTable(name="payment_ticket", joinColumns=@JoinColumn(name="payment_id"))
    @Column(name="ticket_id")
    private List<Long> tickets;
    @Column(name = "user_id")
    private Long userId;
    private String cardNumber;
    private PaymentStatus paymentStatus;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Payment payment = (Payment) o;
        return id != null && Objects.equals(id, payment.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
