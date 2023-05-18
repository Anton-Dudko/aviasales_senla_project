package eu.senla.userservice.exception;

import eu.senla.userservice.exception.custom.AuthenticatException;
import eu.senla.userservice.exception.custom.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.StaleObjectStateException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@Slf4j
@RestControllerAdvice
public class ProjectExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponce notFoundException(NotFoundException ex) {
        log.warn(ex.getMessage());
        return new ErrorResponce(HttpStatus.NOT_FOUND.value(), ex.getMessage());
    }

    @ExceptionHandler(StaleObjectStateException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponce staleObjectStateException(StaleObjectStateException ex) {
        log.warn(ex.getMessage());
        return new ErrorResponce(HttpStatus.CONFLICT.value(), ex.getMessage());
    }

    @ExceptionHandler(AuthenticatException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponce handleAuthenticatException(AuthenticatException ex) {
        log.warn(ex.getMessage());
        return new ErrorResponce(HttpStatus.UNAUTHORIZED.value(), ex.getMessage());
    }
}
