package com.aviasalestickets.exception;

public class TicketNotRefundException extends RuntimeException {
    public TicketNotRefundException(String message) {
        super(message);
    }
}
