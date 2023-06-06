package com.aviasales.finance.external.payment.system.exceptions;

public class ExternalAccountException extends RuntimeException{
    public ExternalAccountException(String message) {
        super(message);
    }
}
