package com.aviasales.finance.dto;

import lombok.Data;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.util.HashMap;
import java.util.Map;

@Data
public class FieldsErrorResponse {
    private Map<String, String> errors = new HashMap<>();

    public FieldsErrorResponse(BindingResult bindingResult) {
        bindingResult.getFieldErrors().forEach(this::addError);
    }

    public FieldsErrorResponse(String field, String message) {
        addError(field, message);
    }

    public void addError(FieldError error) {
        errors.put(error.getField(), error.getDefaultMessage());
    }

    public void addError(String field, String message) {
        errors.put(field, message);
    }
}