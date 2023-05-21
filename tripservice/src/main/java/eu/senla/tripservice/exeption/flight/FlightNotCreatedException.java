package eu.senla.tripservice.exeption.flight;

public class FlightNotCreatedException extends RuntimeException {
    public FlightNotCreatedException(String message) {
        super(message);
    }
}
