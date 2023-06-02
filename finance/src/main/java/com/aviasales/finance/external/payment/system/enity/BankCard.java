package com.aviasales.finance.external.payment.system.enity;

import lombok.*;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.Objects;

@Entity
@Getter
@Setter
@ToString
@RequiredArgsConstructor
public class BankCard {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    private String cardNumber;
    private YearMonth cardDate;
    private String cvv;
    private String cardHolder;
    private BigDecimal accountSum;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        BankCard bankCard = (BankCard) o;
        return id != null && Objects.equals(id, bankCard.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
