package com.aviasales.finance.exception;

public class TicketNotFoundException extends RuntimeException{
    public TicketNotFoundException(String ticketId) {
        super("Such ticket not found. Id - " + ticketId);
    }
}
