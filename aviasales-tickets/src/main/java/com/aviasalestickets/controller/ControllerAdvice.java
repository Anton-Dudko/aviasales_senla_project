package com.aviasalestickets.controller;

import com.aviasalestickets.exception.ErrorDetails;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@Slf4j
@RestControllerAdvice
public class ControllerAdvice {

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
