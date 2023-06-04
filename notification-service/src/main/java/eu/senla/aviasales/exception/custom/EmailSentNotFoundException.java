package eu.senla.aviasales.exception.custom;

/**
 * @author Mikhail.Leonovets
 * @since 05/2023
 */
public class EmailSentNotFoundException extends Exception {
    public EmailSentNotFoundException(String message) {
        super(message);
    }
}
