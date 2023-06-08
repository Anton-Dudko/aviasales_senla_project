package com.aviasales.finance.controller;

import com.aviasales.finance.dto.*;
import com.aviasales.finance.dto.payment.PaymentDto;
import com.aviasales.finance.dto.payment.PaymentFilter;
import com.aviasales.finance.dto.payment.PaymentListDto;
import com.aviasales.finance.dto.payment.TransactionDto;
import com.aviasales.finance.dto.ticket.TicketInfoDto;
import com.aviasales.finance.entity.Payment;
import com.aviasales.finance.service.BlockingCardService;
import com.aviasales.finance.service.PaymentService;
import com.aviasales.finance.service.external.TicketService;
import com.aviasales.finance.service.external.TripService;
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
    private final TripService tripService;

    @Autowired
    public PaymentController(PaymentService paymentService,
                             BlockingCardService blockingCardService, TicketService ticketService, TripService tripService) {
        this.paymentService = paymentService;
        this.blockingCardService = blockingCardService;
        this.ticketService = ticketService;
        this.tripService = tripService;
    }

    @PostMapping
    public ResponseEntity<?> createPayment(@RequestBody @Valid PaymentDto paymentDto, BindingResult bindingResult,
                                           @RequestHeader(name = "userDetails") String userDetails) {
        UserDetailsDto userDetailsDto = paymentService.getUserDetailsFromString(userDetails);

        if (bindingResult.hasErrors()) {
            return new ResponseEntity<>(new FieldsErrorResponse(bindingResult), HttpStatus.BAD_REQUEST);
        }

        logger.info("Checking card details");
        blockingCardService.validateCardDetails(paymentDto.getCardNumber());
        logger.info("Checking ticket");
        List<TicketInfoDto> ticketInfoDto = ticketService.getTicketInfoForPaying(paymentDto.getTickets(), userDetailsDto);
        tripService.checkFlightDateForPaying(ticketInfoDto.stream().map(TicketInfoDto::getFlightId)
                .distinct().collect(Collectors.toList()));

        Payment payment = paymentService.createPayment(paymentDto, ticketInfoDto, userDetailsDto.getUserId());
        TransactionDto transactionDto = paymentService.createTransaction(paymentDto, payment.getAmount());

        KafkaPaymentNotificationDto kafkaPaymentNotificationDto = new KafkaPaymentNotificationDto();
        kafkaPaymentNotificationDto.setEmail(userDetailsDto.getEmail());
        kafkaPaymentNotificationDto.setUserLanguage(userDetailsDto.getLanguage());
        kafkaPaymentNotificationDto.setUserName(userDetailsDto.getUsername());
        kafkaPaymentNotificationDto.setAmountPayable(payment.getAmount());

        payment = paymentService.processPayment(transactionDto, payment, kafkaPaymentNotificationDto);
        ticketService.updateTicketToPaid(paymentDto.getTickets(), userDetails);

        return ResponseEntity.status(HttpStatus.OK).body(new SimpleResponse("Ticket(s) paid. Ticket Id(s) - " + paymentDto
                .getTickets().stream().map(Object::toString).collect(Collectors.joining(", "))
                + ". Payment id - " + payment.getId()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getPaymentInfo(@PathVariable("id") Long paymentId,
                                            @RequestHeader(name = "userDetails") String userDetails) {
        UserDetailsDto userDetailsDto = paymentService.getUserDetailsFromString(userDetails);
        Optional<Payment> payment = paymentService.getPaymentById(paymentId, userDetailsDto);
        if (payment.isPresent()) {
            return ResponseEntity.of(payment);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new
                SimpleResponse("There is no such payment available for you - " + paymentId));
    }


    @GetMapping("/stat")
    public PaymentListDto getPaymentStat(@Valid PaymentFilter paymentFilter, @RequestParam(defaultValue = "0") int page,
                                         @RequestParam(defaultValue = "15") int size,
                                         @RequestHeader(name = "userDetails") String userDetails) {

        UserDetailsDto userDetailsDto = paymentService.getUserDetailsFromString(userDetails);
        PageRequest pageRequest = PageRequest.of(page, size);
        return paymentService.findPayments(paymentFilter, pageRequest, userDetailsDto);
    }

    @PostMapping("/refund/{id}")
    public ResponseEntity<?> refundPayment(@PathVariable("id") long paymentId,
                                           @RequestHeader(name = "userDetails") String userDetails) {
        UserDetailsDto userDetailsDto = paymentService.getUserDetailsFromString(userDetails);
        return paymentService.refundPayment(paymentId, userDetailsDto);
    }
}