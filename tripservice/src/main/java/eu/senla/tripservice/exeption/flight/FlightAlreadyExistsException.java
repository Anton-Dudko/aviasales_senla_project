package eu.senla.tripservice.exeption.flight;

public class FlightAlreadyExistsException extends RuntimeException {
    public FlightAlreadyExistsException(String message) {
        super(message);
    }
}
