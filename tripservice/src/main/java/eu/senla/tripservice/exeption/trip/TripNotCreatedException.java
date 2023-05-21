package eu.senla.tripservice.exeption.trip;

public class TripNotCreatedException extends RuntimeException {
    public TripNotCreatedException(String msg) {
        super(msg);
    }
}
