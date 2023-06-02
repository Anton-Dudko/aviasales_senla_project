package eu.senla.aviasales.controller.advice;

import eu.senla.aviasales.exception.EmailSentNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * @author Mikhail.Leonovets
 * @since 05/2023
 */
@ControllerAdvice
public class NotFoundExceptionControllerAdvice {

    @ExceptionHandler(EmailSentNotFoundException.class)
    public ResponseEntity<?> handleEmailSentNotFoundException(EmailSentNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }

}
