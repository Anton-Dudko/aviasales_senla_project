package com.aviasales.finance.external.payment.system.controller;

import com.aviasales.finance.external.payment.system.dto.PaymentForProcessingDto;
import com.aviasales.finance.external.payment.system.service.PaymentProcessingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/external/payment")
public class PaymentExternalSystem {
    private final PaymentProcessingService paymentProcessingService;

    @Autowired
    public PaymentExternalSystem(PaymentProcessingService paymentProcessingService) {
        this.paymentProcessingService = paymentProcessingService;
    }

    @PostMapping
    public ResponseEntity<?> makePayment(@RequestBody PaymentForProcessingDto paymentForProcessingDto) {
        paymentProcessingService.processPayment(paymentForProcessingDto);
        return ResponseEntity.status(HttpStatus.OK).body("Payment done");
    }
}
