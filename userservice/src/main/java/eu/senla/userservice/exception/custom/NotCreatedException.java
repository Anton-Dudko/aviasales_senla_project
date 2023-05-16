package eu.senla.userservice.exception.custom;

public class NotCreatedException extends RuntimeException {

    public NotCreatedException() {
    }

    public NotCreatedException(String message) {
        super(message);
    }
}
