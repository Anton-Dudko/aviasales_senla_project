package eu.senla.aviasales.exception;

import eu.senla.aviasales.exception.custom.EmailSentNotFoundException;
import eu.senla.aviasales.exception.custom.IncorrectDataException;
import eu.senla.aviasales.exception.custom.TopicNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * @author Mikhail.Leonovets
 * @since 05/2023
 */
@Slf4j
@RestControllerAdvice
public class NotificationExceptionHandler {

    @ExceptionHandler(EmailSentNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleEmailSentNotFoundException(EmailSentNotFoundException ex) {
        log.warn(ex.getMessage());
        return new ErrorResponse(HttpStatus.NOT_FOUND.value(), ex.getMessage());
    }

    @ExceptionHandler(IncorrectDataException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleIncorrectDataException(IncorrectDataException ex) {
        log.warn(ex.getMessage());
        return new ErrorResponse(HttpStatus.BAD_REQUEST.value(), ex.getMessage());
    }

    @ExceptionHandler(TopicNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleTopicNotFoundException(TopicNotFoundException ex) {
        log.warn(ex.getMessage());
        return new ErrorResponse(HttpStatus.NOT_FOUND.value(), ex.getMessage());
    }

}
