package eu.senla.aviasales.exception.custom;

/**
 * @author Mikhail.Leonovets
 * @since 05/2023
 */
public class NotFoundException extends RuntimeException {
    public NotFoundException(String message) {
        super(message);
    }
}
