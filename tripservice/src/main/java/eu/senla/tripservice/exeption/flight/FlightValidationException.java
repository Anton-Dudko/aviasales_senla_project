package eu.senla.tripservice.exeption.flight;

public class FlightValidationException extends RuntimeException {
    public FlightValidationException(String message) {
        super(message);
    }
}
