package com.aviasales.finance.enums;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.stream.Stream;

@Converter(autoApply = true)
public class PaymentStatusConverter implements AttributeConverter<PaymentStatus, Integer> {

    @Override
    public Integer convertToDatabaseColumn(PaymentStatus paymentStatus) {
        if (paymentStatus == null) {
            return null;
        } else {
            return paymentStatus.getId();
        }
    }

    @Override
    public PaymentStatus convertToEntityAttribute(Integer id) {
        if (id == null) {
            return null;
        }

        return Stream.of(PaymentStatus.values()).filter(paymentStatus -> paymentStatus.getId().equals(id))
                .findFirst().orElseThrow(IllegalArgumentException::new);
    }
}
