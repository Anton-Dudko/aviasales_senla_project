package com.aviasales.finance.controller;

import com.aviasales.finance.dto.*;
import com.aviasales.finance.entity.Payment;
import com.aviasales.finance.service.BlockingCardService;
import com.aviasales.finance.service.PaymentService;
import com.aviasales.finance.service.external.TicketService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.catalina.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
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
    private final ObjectMapper objectMapper;

    @Autowired
    public PaymentController(PaymentService paymentService,
                             BlockingCardService blockingCardService, TicketService ticketService, ObjectMapper objectMapper) {
        this.paymentService = paymentService;
        this.blockingCardService = blockingCardService;
        this.ticketService = ticketService;
        this.objectMapper = objectMapper;
    }

    @PostMapping
    public ResponseEntity<?> createPayment(@RequestBody @Valid PaymentDto paymentDto, BindingResult bindingResult,
                                           @RequestHeader String userDetails) {
        UserDetailsDto userDetailsDto;
        try {
            userDetailsDto = objectMapper.readValue(userDetails, UserDetailsDto.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        if (bindingResult.hasErrors()) {
            return new ResponseEntity<>(new FieldsErrorResponse(bindingResult), HttpStatus.BAD_REQUEST);
        }

        logger.info("Checking card details");
        blockingCardService.validateCardDetails(paymentDto.getCardNumber());
        logger.info("Checking ticket");
        List<TicketInfoDto> ticketInfoDto = ticketService.getTicketInfoForPaying(paymentDto.getTickets());

        Payment payment = paymentService.createPayment(paymentDto, ticketInfoDto, userDetailsDto.getUserId());
        TransactionDto transactionDto = paymentService.createTransaction(paymentDto, payment.getAmount());

        KafkaPaymentNotificationDto kafkaPaymentNotificationDto = new KafkaPaymentNotificationDto();
        kafkaPaymentNotificationDto.setEmail(userDetailsDto.getEmail());
        kafkaPaymentNotificationDto.setUserLanguage(userDetailsDto.getLanguage());
        kafkaPaymentNotificationDto.setUserName(userDetailsDto.getUsername());
        kafkaPaymentNotificationDto.setAmountPayable(payment.getAmount());

        paymentService.processPayment(transactionDto, payment, kafkaPaymentNotificationDto);
        ticketService.updateTicketToPaid(paymentDto.getTickets());

        return ResponseEntity.status(HttpStatus.OK).body(new SimpleResponse("Ticket(s) paid. Ticket Id(s) - " + paymentDto
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
    public PaymentListDto getPaymentStat(PaymentFilter paymentFilter, @RequestParam(defaultValue = "0") int page,
                                        @RequestParam(defaultValue = "15") int size,
                                         @RequestHeader(name = "userDetails") String userDetails) {
        try {
            UserDetailsDto userDetailsDto = objectMapper.readValue(userDetails, UserDetailsDto.class);
            PageRequest pageRequest = PageRequest.of(page, size);
            return paymentService.findPayments(paymentFilter, pageRequest, userDetailsDto);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

    }

    @PostMapping("/refund/{id}")
    public ResponseEntity<?> refundPayment(@PathVariable("id") long paymentId,
                                           @RequestHeader(name = "userDetails") String userDetails) {
        try {
            UserDetailsDto userDetailsDto = objectMapper.readValue(userDetails, UserDetailsDto.class);
            return paymentService.refundPayment(paymentId, userDetailsDto);

        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}