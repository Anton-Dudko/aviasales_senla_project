package com.aviasales.finance.external.payment.system.exceptions;

import com.aviasales.finance.external.payment.system.controller.AccountController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(assignableTypes = AccountController.class)
public class AccountsHandler {
    @ExceptionHandler(ExternalAccountException.class)
    public ResponseEntity<Object> handleExternalAccountException(ExternalAccountException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }
}
