package eu.senla.aviasales.exception.custom;

/**
 * @author Mikhail.Leonovets
 * @since 05/2023
 */
public class TopicNotFoundException extends RuntimeException {
    public TopicNotFoundException(String message) {
        super(message);
    }
}
