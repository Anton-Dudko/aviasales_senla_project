package eu.senla.tripservice.exeption.ticket;

public class RequestException extends RuntimeException {
    public RequestException(String message) {
        super(message);
    }
}
