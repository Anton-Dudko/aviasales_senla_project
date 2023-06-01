package com.aviasales.finance.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class KafkaPaymentNotificationDto {
    private String userLanguage;
    private String email;
    private String userName;
    private String paymentInfo;
    private BigDecimal amountPayable;
    private LocalDate paymentDate;
}
