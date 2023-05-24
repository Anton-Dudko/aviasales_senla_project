package com.aviasales.finance.dto;

import lombok.Data;

@Data
public class SimpleErrorResponse {
    private String error;

    public SimpleErrorResponse(String error) {
        this.error = error;
    }
}
