package com.aviasales.finance.external.payment.system.exceptions;

import com.aviasales.finance.external.payment.system.controller.PaymentExternalSystem;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(assignableTypes = PaymentExternalSystem.class)
public class PaymentProcessingErrorHandler {

    @ExceptionHandler(BankCardNotFoundException.class)
    public ResponseEntity<Object> handleBankCardNotFoundException(BankCardNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    @ExceptionHandler(CardDetailsMismatchException.class)
    public ResponseEntity<Object> handleCardDetailsMismatchException(CardDetailsMismatchException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    @ExceptionHandler(InsufficientBalanceException.class)
    public ResponseEntity<Object> handleInsufficientFundsException(InsufficientBalanceException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }
}
