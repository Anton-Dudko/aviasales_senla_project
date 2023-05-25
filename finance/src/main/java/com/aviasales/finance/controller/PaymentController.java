package com.aviasales.finance.controller;

import com.aviasales.finance.dto.FieldsErrorResponse;
import com.aviasales.finance.dto.PaymentDto;
import com.aviasales.finance.dto.SimpleResponse;
import com.aviasales.finance.dto.TicketInfoDto;
import com.aviasales.finance.service.BlockingCardService;
import com.aviasales.finance.service.PaymentSystemService;
import com.aviasales.finance.service.external.TicketService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
public class PaymentController {
    private static final Logger logger = LoggerFactory.getLogger(PaymentController.class);
    private final PaymentSystemService paymentSystemService;
    private final BlockingCardService blockingCardService;
    private final TicketService ticketService;

    @Autowired
    public PaymentController(PaymentSystemService paymentSystemService,
                             BlockingCardService blockingCardService, TicketService ticketService) {
        this.paymentSystemService = paymentSystemService;
        this.blockingCardService = blockingCardService;
        this.ticketService = ticketService;
    }

    @PostMapping("/payment")
    public ResponseEntity<?> createPayment(@RequestBody @Valid PaymentDto paymentDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return new ResponseEntity<>(new FieldsErrorResponse(bindingResult), HttpStatus.BAD_REQUEST);
        }

        try {
            logger.info("Checking card details");
            blockingCardService.validateCardDetails(paymentDto.getCardNumber());

            logger.info("Checking ticked");
            TicketInfoDto ticketInfoDto = ticketService.getTicketInfo(paymentDto.getTicketId());
            ticketService.checkTicketNotPaid(ticketInfoDto);

            logger.info("sending to payment system");
            paymentSystemService.sendPaymentToExternalPaymentSystem(paymentDto, ticketInfoDto);
            ticketService.updateTicketToPaid(ticketInfoDto.getTicketId());
        } catch (RuntimeException e) {
            logger.info("Send failure message to KAFKA");
            throw e;
        }

        logger.info("Send success message to KAFKA");
        return ResponseEntity.status(HttpStatus.OK).body(new SimpleResponse("Ticked paid. Id - " + paymentDto.getTicketId()));
    }
}