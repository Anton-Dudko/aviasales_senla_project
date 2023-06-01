package com.aviasales.finance.dto;

import com.aviasales.finance.enums.PaymentStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Data
public class PaymentFilter {
    private Long ticketId;
    @JsonFormat
    private PaymentStatus paymentStatus;
    @DateTimeFormat(pattern = "dd/MM/yy")
    private LocalDate startDate;
    @DateTimeFormat(pattern = "dd/MM/yy")
    private LocalDate endDate;
    private String amount;

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = PaymentStatus.valueOf(paymentStatus.toUpperCase());
    }
}
