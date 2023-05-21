package eu.senla.tripservice.exeption;

public class TripNotCreatedException extends RuntimeException {
    public TripNotCreatedException(String msg) {
        super(msg);
    }
}
