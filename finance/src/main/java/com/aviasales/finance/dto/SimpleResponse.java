package com.aviasales.finance.dto;

import lombok.Data;

@Data
public class SimpleResponse {
    private String message;

    public SimpleResponse(String message) {
        this.message = message;
    }
}
