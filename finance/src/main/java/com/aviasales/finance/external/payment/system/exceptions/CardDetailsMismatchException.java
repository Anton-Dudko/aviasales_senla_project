package com.aviasales.finance.external.payment.system.exceptions;

public class CardDetailsMismatchException extends RuntimeException{
    public CardDetailsMismatchException(String message) {
        super(message);
    }
}
