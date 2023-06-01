package com.aviasales.finance.controller;

import com.aviasales.finance.dto.*;
import com.aviasales.finance.entity.Payment;
import com.aviasales.finance.service.BlockingCardService;
import com.aviasales.finance.service.PaymentService;
import com.aviasales.finance.service.external.TicketService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/payment")
public class PaymentController {
    private static final Logger logger = LoggerFactory.getLogger(PaymentController.class);
    private final PaymentService paymentService;
    private final BlockingCardService blockingCardService;
    private final TicketService ticketService;

    @Autowired
    public PaymentController(PaymentService paymentService,
                             BlockingCardService blockingCardService, TicketService ticketService) {
        this.paymentService = paymentService;
        this.blockingCardService = blockingCardService;
        this.ticketService = ticketService;
    }

    @PostMapping("/payment")
    public ResponseEntity<?> createPayment(@RequestBody @Valid PaymentDto paymentDto, BindingResult bindingResult,
                                           @CookieValue(name = "username") String username,
                                           @CookieValue(name = "email") String email,
                                           @CookieValue(name = "language") String language,
                                           @CookieValue(name = "id") String userId) {
        if (bindingResult.hasErrors()) {
            return new ResponseEntity<>(new FieldsErrorResponse(bindingResult), HttpStatus.BAD_REQUEST);
        }

        logger.info("Checking card details");
        blockingCardService.validateCardDetails(paymentDto.getCardNumber());
        logger.info("Checking ticket");
        List<TicketInfoDto> ticketInfoDto = ticketService.getTicketInfoForPaying(paymentDto.getTickets());

        Payment payment = paymentService.createPayment(paymentDto, ticketInfoDto, userId);
        TransactionDto transactionDto = paymentService.createTransaction(paymentDto, payment.getAmount());

        KafkaPaymentNotificationDto kafkaPaymentNotificationDto = new KafkaPaymentNotificationDto();
        kafkaPaymentNotificationDto.setEmail(email);
        kafkaPaymentNotificationDto.setUserLanguage(language);
        kafkaPaymentNotificationDto.setUserName(username);
        kafkaPaymentNotificationDto.setAmountPayable(payment.getAmount());

        paymentService.processPayment(transactionDto, payment, kafkaPaymentNotificationDto);
        ticketService.updateTicketToPaid(paymentDto.getTickets());

        return ResponseEntity.status(HttpStatus.OK).body(new SimpleResponse("Ticket(s) paid. Id - " + paymentDto
                .getTickets().stream().map(Object::toString).collect(Collectors.joining(", "))));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getPaymentInfo(@PathVariable("id") Long paymentId) {
        Optional<Payment> payment =  paymentService.getPaymentById(paymentId);
        if (payment.isPresent()) {
            return ResponseEntity.of(payment);
        }
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(new
                SimpleResponse("There is no such payment - " + paymentId));
    }


    @GetMapping("/stat")
    public List<Payment> getPaymentStat(PaymentFilter paymentFilter) {
        return paymentService.findPayments(paymentFilter);
    }

    @GetMapping("/refund/{id}")
    public ResponseEntity<?> refundPayment(@PathVariable("id") int paymentId) {
        //ToDo make refund here
        return ResponseEntity.notFound().build();
    }
}