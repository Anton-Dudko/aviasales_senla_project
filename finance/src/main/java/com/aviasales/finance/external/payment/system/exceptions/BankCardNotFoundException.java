package com.aviasales.finance.external.payment.system.exceptions;

public class BankCardNotFoundException extends RuntimeException {
    public BankCardNotFoundException(String message) {
        super(message);
    }
}
