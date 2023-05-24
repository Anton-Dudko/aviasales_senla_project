package com.aviasales.finance.exception;

public class TickedAlreadyPaidException extends RuntimeException{
    public TickedAlreadyPaidException(String ticketId) {
        super("Ticket already in Paid status. Id - " + ticketId);
    }
}
