package com.aviasales.finance.dto.payment;

import lombok.Data;

@Data
public class ExternalPaymentResponse {
    private String responseBody;
    private Exception exception;

    public ExternalPaymentResponse(String responseBody) {
        this.responseBody = responseBody;
    }

    public ExternalPaymentResponse(Exception exception, String responseBody) {
        this.exception = exception;
    }

    public String getResponseBody() {
        return responseBody;
    }

    public Exception getException() {
        return exception;
    }

    public boolean hasException() {
        return exception != null;
    }
}
