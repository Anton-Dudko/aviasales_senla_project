package eu.senla.tripservice.exeption.airplane;

public class AirplaneNotFoundException extends RuntimeException {
    public AirplaneNotFoundException(String message) {
        super(message);
    }
}
