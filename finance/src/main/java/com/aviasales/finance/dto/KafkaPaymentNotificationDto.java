package com.aviasales.finance.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class KafkaPaymentNotificationDto {
    private String USER_LANGUAGE;
    private String EMAIL;
    private String USERNAME;
    private String PAYMENT_INFO;
    private double AMOUNT_PAYABLE;
    private LocalDate PAYMENT_DATE;
}
