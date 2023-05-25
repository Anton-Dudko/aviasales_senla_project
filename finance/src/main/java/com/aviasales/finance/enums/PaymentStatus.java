package com.aviasales.finance.enums;

public enum PaymentStatus {
    PENDING(1),
    PAID(2),
    FAILED(3);

    private Integer id;

    PaymentStatus(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }
}