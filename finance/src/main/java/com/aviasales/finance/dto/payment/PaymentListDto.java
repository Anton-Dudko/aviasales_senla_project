package com.aviasales.finance.dto.payment;

import com.aviasales.finance.entity.Payment;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class PaymentListDto {
    private long total;
    private List<Payment> payments;
}
