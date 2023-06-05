package com.aviasalestickets.controller;

import com.aviasalestickets.exception.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@Slf4j
@RestControllerAdvice
public class ControllerAdvice {

    @ExceptionHandler(TicketNotFoundException.class)
    public ResponseEntity<ErrorDetails> handleTicketNotFoundException(TicketNotFoundException e) {
        return build(e.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(TicketNotCreatedException.class)
    public ResponseEntity<ErrorDetails> handleTicketNotFoundException(TicketNotCreatedException e) {
        return build(e.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(TicketNotPaidException.class)
    public ResponseEntity<ErrorDetails> handleTicketNotFoundException(TicketNotPaidException e) {
        return build(e.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(TicketNotDeletedReservationException.class)
    public ResponseEntity<ErrorDetails> handleTicketNotFoundException(TicketNotDeletedReservationException e) {
        return build(e.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(TicketNotBookedException.class)
    public ResponseEntity<ErrorDetails> handleTicketNotFoundException(TicketNotBookedException e) {
        return build(e.getMessage(), HttpStatus.BAD_REQUEST);
    }

    private ResponseEntity<ErrorDetails> build(String message, HttpStatus status) {
        log.error("Error msg: {}", message);
        return new ResponseEntity<>(ErrorDetails.builder()
                .message(message)
                .status(status.name())
                .createdAt(LocalDateTime.now())
                .build(),
                status);
    }
}
