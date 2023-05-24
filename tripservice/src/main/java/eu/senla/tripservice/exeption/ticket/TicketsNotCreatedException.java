package eu.senla.tripservice.exeption.ticket;

public class TicketsNotCreatedException extends RuntimeException {
    public TicketsNotCreatedException(String message) {
        super(message);
    }
}
