package com.aviasalestickets.controller;

import com.aviasalestickets.exception.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.UnexpectedTypeException;
import java.time.LocalDateTime;

@Slf4j
@RestControllerAdvice
public class ControllerAdvice {

    @ExceptionHandler(TicketNotFoundException.class)
    public ResponseEntity<ErrorDetails> handleTicketNotFoundException(TicketNotFoundException e) {
        return build(e.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler({TicketNotCreatedException.class})
    public ResponseEntity<ErrorDetails> handleTicketNotCreatedException(TicketNotCreatedException e) {
        return build(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(TicketNotPaidException.class)
    public ResponseEntity<ErrorDetails> handleTicketNotPaidException(TicketNotPaidException e) {
        return build(e.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(TicketNotDeletedReservationException.class)
    public ResponseEntity<ErrorDetails> handleTicketNotDeletedReservationException(TicketNotDeletedReservationException e) {
        return build(e.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(TicketNotBookedException.class)
    public ResponseEntity<ErrorDetails> handleTicketNotBookedException(TicketNotBookedException e) {
        return build(e.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({TicketNotRefundException.class, UnexpectedTypeException.class})
    public ResponseEntity<ErrorDetails> handleTicketNotRefundException(RuntimeException e) {
        return build(e.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UserDetailsException.class)
    public ResponseEntity<ErrorDetails> handleUserDetailsException(UserDetailsException e) {
        return build(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private ResponseEntity<ErrorDetails> build(String message, HttpStatus status) {
        log.error("Error message: {}", message);
        return new ResponseEntity<>(ErrorDetails.builder()
                .message(message)
                .status(status.name())
                .createdAt(LocalDateTime.now())
                .build(),
                status);
    }
}
