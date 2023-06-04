package eu.senla.aviasales.exception.custom;

/**
 * @author Mikhail.Leonovets
 * @since 05/2023
 */
public class TopicNotFoundException extends Exception {
    public TopicNotFoundException(String message) {
        super(message);
    }
}
