package eu.senla.aviasales.exception;

/**
 * @author Mikhail.Leonovets
 * @since 05/2023
 */
public class EmailSentNotFoundException extends Exception {
    public EmailSentNotFoundException(String message) {
        super(message);
    }
}
