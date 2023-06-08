package com.aviasales.finance.converter;

import com.aviasales.finance.dto.payment.PaymentDto;
import com.aviasales.finance.entity.Payment;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

@Component
public class PaymentConverter {

    public Payment convertToEntity(PaymentDto paymentDto) {
        Payment payment = new Payment();
        BeanUtils.copyProperties(paymentDto, payment);
        return payment;
    }
}
