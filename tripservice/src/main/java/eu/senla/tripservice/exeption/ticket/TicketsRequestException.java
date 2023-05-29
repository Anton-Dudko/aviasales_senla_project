package eu.senla.tripservice.exeption.ticket;

public class TicketsRequestException extends RuntimeException {
    public TicketsRequestException(String message) {
        super(message);
    }
}
