package eu.senla.userservice.exception.custom;

public class AuthenticatException extends RuntimeException {

    public AuthenticatException() {
    }

    public AuthenticatException(String message) {
        super(message);
    }
}
