package com.aviasales.finance.exception.handler;

import com.aviasales.finance.dto.FieldsErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.format.DateTimeParseException;

@RestControllerAdvice
public class DateExceptionHandler {

    @ExceptionHandler(DateTimeParseException.class)
    public ResponseEntity<Object> handleDateTimeException(DateTimeParseException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new FieldsErrorResponse("cvv",
                "Invalid format of the date specified, should be 'MM/yy'"));
    }
}
