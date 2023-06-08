package com.aviasales.finance.service;

import com.aviasales.finance.converter.PaymentConverter;
import com.aviasales.finance.dto.*;
import com.aviasales.finance.dto.payment.*;
import com.aviasales.finance.dto.ticket.TicketInfoDto;
import com.aviasales.finance.entity.Payment;
import com.aviasales.finance.enums.PaymentStatus;
import com.aviasales.finance.enums.UserRole;
import com.aviasales.finance.exception.ExternalPaymentSystemException;
import com.aviasales.finance.exception.UserDetailsException;
import com.aviasales.finance.repository.PaymentRepository;
import com.aviasales.finance.service.external.KafkaService;
import com.aviasales.finance.service.external.TicketService;
import com.aviasales.finance.service.external.TripService;
import com.aviasales.finance.utils.UrlUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tennaito.rsql.jpa.JpaPredicateVisitor;
import cz.jirutka.rsql.parser.RSQLParser;
import cz.jirutka.rsql.parser.ast.Node;
import cz.jirutka.rsql.parser.ast.RSQLVisitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import javax.persistence.EntityManager;
import javax.persistence.criteria.Predicate;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
public class PaymentService {
    private static final Logger logger = LoggerFactory.getLogger(PaymentService.class);
    private final PaymentConverter paymentConverter;
    private final PaymentRepository paymentRepository;
    private final RestTemplate restTemplate;
    private final UrlUtils urlUtils;
    private final KafkaService kafkaService;
    private final TicketService ticketService;
    private final EntityManager entityManager;
    private final ObjectMapper objectMapper;
    private final TripService tripService;

    @Autowired
    public PaymentService(PaymentConverter paymentConverter, PaymentRepository paymentRepository,
                          RestTemplate restTemplate, UrlUtils urlUtils, KafkaService kafkaService,
                          TicketService ticketService, EntityManager entityManager, ObjectMapper objectMapper, TripService tripService) {
        this.paymentConverter = paymentConverter;
        this.paymentRepository = paymentRepository;
        this.restTemplate = restTemplate;
        this.urlUtils = urlUtils;
        this.kafkaService = kafkaService;
        this.ticketService = ticketService;
        this.entityManager = entityManager;
        this.objectMapper = objectMapper;
        this.tripService = tripService;
    }

    private ExternalPaymentResponse sendPaymentToExternalPaymentSystem(TransactionDto transactionDto, Payment payment) {
        logger.info("Sending transaction to payment external system");
        String baseUrl = urlUtils.getRootUrl();
        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(baseUrl + "/external/payment");

        try {
            ResponseEntity<String> responseEntity = restTemplate.exchange(builder.toUriString(), HttpMethod.POST,
                    new HttpEntity<>(transactionDto, new HttpHeaders()), String.class);
            return new ExternalPaymentResponse(responseEntity.getBody());
        } catch (HttpClientErrorException | HttpServerErrorException paymentExternalException) {
            return new ExternalPaymentResponse(paymentExternalException, paymentExternalException.getResponseBodyAsString());
        }
    }

    public Payment processPayment(TransactionDto transactionDto, Payment payment,
                               KafkaPaymentNotificationDto kafkaNotification) {
        ExternalPaymentResponse externalPaymentResponse = sendPaymentToExternalPaymentSystem(transactionDto, payment);
        kafkaNotification.setPaymentDate(LocalDate.now());
        if (externalPaymentResponse.hasException()) {
            logger.warn("Payment failed via external service, updating payment status to failed, payment id - "
                    + payment.getId());
            payment.setPaymentStatus(PaymentStatus.FAILED);
            paymentRepository.save(payment);
            kafkaNotification.setPaymentInfo("payment failed via card number - " + transactionDto.getCardNumber());
            kafkaService.sendErrorMessage(kafkaNotification);
            throw new ExternalPaymentSystemException("Error received from external payment service: " +
                    externalPaymentResponse.getException().getMessage());
        }

        logger.info("Payment done via external service, updating payment status to paid, payment id - "
                + payment.getId());
        payment.setPaymentStatus(PaymentStatus.PAID);
        payment = paymentRepository.save(payment);
        kafkaNotification.setPaymentInfo("payment done via card number - " + transactionDto.getCardNumber());
        kafkaService.sendSuccessMessage(kafkaNotification);
        return payment;
    }

    public Payment createPayment(PaymentDto paymentDto, List<TicketInfoDto> ticketList, long userId) {
        Payment payment = paymentConverter.convertToEntity(paymentDto);
        payment.setPaymentCreationDateTime(LocalDateTime.now());
        payment.setPaymentStatus(PaymentStatus.PENDING);
        BigDecimal sumToPay = ticketList.stream().map(TicketInfoDto::getPrice)
                .reduce(new BigDecimal(0), BigDecimal::add);
        payment.setAmount(sumToPay);
        payment.setUserId(userId);
        return paymentRepository.save(payment);
    }

    public TransactionDto createTransaction(PaymentDto paymentDto, BigDecimal sumToPay) {
        //ToDo add mapper
        TransactionDto transactionDto = new TransactionDto();
        transactionDto.setCardNumber(paymentDto.getCardNumber());
        transactionDto.setCardHolder(paymentDto.getCardHolder());
        transactionDto.setCardDate(paymentDto.getCardDate());
        transactionDto.setCvv(paymentDto.getCvv());
        transactionDto.setSum(sumToPay);
        return transactionDto;
    }


    public Optional<Payment> getPaymentById(Long paymentId, UserDetailsDto userDetailsDto) {
        if (userDetailsDto.getRole().equals(UserRole.ROLE_ADMIN)) {
            return paymentRepository.findById(paymentId);
        }
        return paymentRepository.findByIdAndUserId(paymentId, userDetailsDto.getUserId());
    }

    public PaymentListDto findPayments(PaymentFilter filter, PageRequest pageRequest, UserDetailsDto userDetailsDto) {
        Specification<Payment> spec = Specification.where(null);

        if (userDetailsDto.getRole().equals(UserRole.ROLE_USER)) {
            spec = spec.and((root, query, builder) ->
                    builder.equal(root.get("userId"), userDetailsDto.getUserId()));
        }

        if (filter.getTicketId() != null) {
            spec = spec.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.isMember(filter.getTicketId(), root.get("tickets")));
        }

        if (filter.getPaymentStatus() != null) {
            spec = spec.and((root, query, builder) ->
                    builder.equal(root.get("paymentStatus"), filter.getPaymentStatus().getId()));
        }

        if (filter.getStartDate() != null) {
            spec = spec.and((root, query, builder) ->
                    builder.greaterThanOrEqualTo(root.get("paymentCreationDateTime"),
                            filter.getStartDate().atStartOfDay()));
        }

        if (filter.getEndDate() != null) {
            spec = spec.and((root, query, builder) ->
                    builder.lessThanOrEqualTo(root.get("paymentCreationDateTime"),
                            filter.getEndDate().atTime(LocalTime.MAX)));
        }

        if (filter.getAmount() != null) {
            String rsql = "amount" + filter.getAmount();
            //toDo add validation of incorrect filter
            Node node = new RSQLParser().parse(rsql);

            spec = spec.and((root, query, builder) -> {
                RSQLVisitor<Predicate, EntityManager> visitor = new JpaPredicateVisitor<>().defineRoot(root);
                Predicate predicate = node.accept(visitor, entityManager);
                return builder.and(predicate);
            });

        }

        Page<Payment> payments = paymentRepository.findAll(spec, pageRequest);
        PaymentListDto paymentListDto = new PaymentListDto();
        paymentListDto.setPayments(payments.getContent());
        paymentListDto.setTotal(payments.getTotalElements());
        return paymentListDto;
    }

    public ResponseEntity<?> refundPayment(long id, UserDetailsDto userDetailsDto) {
        Optional<Payment> paymentOpt = paymentRepository.findByIdAndPaymentStatus(id, PaymentStatus.PAID);
        logger.info("Checking payment exists");
        if (paymentOpt.isEmpty()) {
            return ResponseEntity.badRequest().body(new SimpleErrorResponse("No such payment in PAID status"));
        }

        Payment payment = paymentOpt.get();
        logger.info("Checking user match");
        if (userDetailsDto.getRole().equals(UserRole.ROLE_USER) && !payment.getUserId().equals(userDetailsDto.getUserId())) {
            return ResponseEntity.badRequest().body(new SimpleErrorResponse("This payment was made by other user"));
        }

        List<TicketInfoDto> tickets = ticketService.getTicketInfo(payment.getTickets());
        List<Long> flights = tickets.stream().map(TicketInfoDto::getFlightId).distinct().toList();
        tripService.checkTripDateForRefund(flights);

        RefundExternalDto refundExternalDto = new RefundExternalDto(payment.getCardNumber(), payment.getAmount());
        ExternalPaymentResponse externalPaymentResponse = sendRefundToExternalPaymentSystem(refundExternalDto);
        if (externalPaymentResponse.hasException()) {
            return ResponseEntity.badRequest().body(new SimpleErrorResponse("Error during refund received from external payment system - "
                    + externalPaymentResponse.getException().getMessage()));
        }

        payment.setPaymentStatus(PaymentStatus.REFUND);
        paymentRepository.save(payment);

        KafkaPaymentNotificationDto kafkaPaymentNotificationDto = new KafkaPaymentNotificationDto();
        kafkaPaymentNotificationDto.setEmail(userDetailsDto.getEmail());
        kafkaPaymentNotificationDto.setUserLanguage(userDetailsDto.getLanguage());
        kafkaPaymentNotificationDto.setUserName(userDetailsDto.getUsername());
        kafkaPaymentNotificationDto.setAmountPayable(payment.getAmount());
        kafkaPaymentNotificationDto.setPaymentDate(LocalDate.now());
        kafkaPaymentNotificationDto.setPaymentInfo(" " + payment.getId() + " on card " + payment.getCardNumber() + ". Ticket refund was made (tickets id)" + Arrays.toString(payment.getTickets().toArray()));

        kafkaService.sendRefund(kafkaPaymentNotificationDto);
        ticketService.updateTicketToRefund(payment.getTickets());
        return ResponseEntity.status(HttpStatus.OK).body(new SimpleResponse("Refund was made for payment - " + payment.getId()));
    }

    public ExternalPaymentResponse sendRefundToExternalPaymentSystem(RefundExternalDto refundExternalDto) {
        logger.info("Sending refund transaction to payment external system");
        String baseUrl = urlUtils.getRootUrl();
        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(baseUrl + "/external/payment/refund");
        try {
            ResponseEntity<String> responseEntity = restTemplate.exchange(builder.toUriString(), HttpMethod.POST,
                    new HttpEntity<>(refundExternalDto, new HttpHeaders()), String.class);
            return new ExternalPaymentResponse(responseEntity.getBody());
        } catch (HttpClientErrorException | HttpServerErrorException paymentExternalException) {
            return new ExternalPaymentResponse(paymentExternalException, paymentExternalException.getResponseBodyAsString());
        }
    }

    public UserDetailsDto getUserDetailsFromString(String userDetails) {
        try {
            return objectMapper.readValue(userDetails, UserDetailsDto.class);
        } catch (JsonProcessingException e) {
            logger.error("error processing user details string received from gateway");
            throw new UserDetailsException("Error occurs during user details processing", e);
        }
    }
}
