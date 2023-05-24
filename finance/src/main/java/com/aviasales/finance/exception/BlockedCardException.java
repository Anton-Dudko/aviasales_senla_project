package com.aviasales.finance.exception;

public class BlockedCardException extends RuntimeException {
    public BlockedCardException(String message) {
        super(message);
    }
}
