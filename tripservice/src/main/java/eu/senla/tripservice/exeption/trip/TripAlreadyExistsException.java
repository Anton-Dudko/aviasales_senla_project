package eu.senla.tripservice.exeption.trip;

public class TripAlreadyExistsException extends RuntimeException {
    public TripAlreadyExistsException(String message) {
        super(message);
    }
}
