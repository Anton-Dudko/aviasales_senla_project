package com.aviasalestickets.exception;

public class TicketNotPaidException extends RuntimeException{
    public TicketNotPaidException(String message) {
        super(message);
    }
}
