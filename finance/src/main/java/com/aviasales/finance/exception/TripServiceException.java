package com.aviasales.finance.exception;

public class TripServiceException extends RuntimeException {
    public TripServiceException(String message, Throwable cause) {
        super(message, cause);
    }

    public TripServiceException(String message) {
        super(message);
    }
}
