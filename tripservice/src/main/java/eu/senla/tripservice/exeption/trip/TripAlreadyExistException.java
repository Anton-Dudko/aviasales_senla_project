package eu.senla.tripservice.exeption.trip;

public class TripAlreadyExistException extends RuntimeException {
    public TripAlreadyExistException(String message) {
        super(message);
    }
}
