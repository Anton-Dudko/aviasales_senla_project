package com.aviasales.finance.exception;

import com.aviasales.finance.dto.SimpleErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class BlockCardExceptionHandler {

    @ExceptionHandler(BlockedCardException.class)
    public ResponseEntity<Object> handleBlockedCardException(BlockedCardException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new SimpleErrorResponse(ex.getMessage()));
    }
}
