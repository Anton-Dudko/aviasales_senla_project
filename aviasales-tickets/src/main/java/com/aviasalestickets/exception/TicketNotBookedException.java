package com.aviasalestickets.exception;

public class TicketNotBookedException extends RuntimeException{
    public TicketNotBookedException(String message) {
        super(message);
    }
}
