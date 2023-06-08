package com.aviasales.finance.dto.payment;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class RefundExternalDto {
    private String cardNumber;
    private BigDecimal amount;
}
