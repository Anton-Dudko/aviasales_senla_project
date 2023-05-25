package com.aviasales.finance.exception;

import com.aviasales.finance.controller.PaymentController;
import com.aviasales.finance.dto.SimpleErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(assignableTypes = PaymentController.class)
public class ExceptionsHandler {
    @ExceptionHandler(BlockedCardException.class)
    public ResponseEntity<Object> handleBlockedCardException(BlockedCardException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new SimpleErrorResponse(ex.getMessage()));
    }

    @ExceptionHandler(TicketNotFoundException.class)
    public ResponseEntity<Object> handleTicketNotFoundException (TicketNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new SimpleErrorResponse(ex.getMessage()));
    }

    @ExceptionHandler(TickedAlreadyPaidException.class)
    public ResponseEntity<Object> handleTicketAlreadyPaidException(TickedAlreadyPaidException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new SimpleErrorResponse(ex.getMessage()));
    }

    @ExceptionHandler(ExternalPaymentSystemException.class)
    public ResponseEntity<Object> handleExternalPaymentSystemException(ExternalPaymentSystemException ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new SimpleErrorResponse(ex.getMessage()));
    }
}
