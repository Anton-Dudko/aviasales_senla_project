package com.aviasales.finance.service;

import com.aviasales.finance.converter.PaymentConverter;
import com.aviasales.finance.dto.*;
import com.aviasales.finance.entity.Payment;
import com.aviasales.finance.enums.PaymentStatus;
import com.aviasales.finance.exception.ExternalPaymentSystemException;
import com.aviasales.finance.repository.PaymentRepository;
import com.aviasales.finance.service.external.KafkaService;
import com.aviasales.finance.utils.UrlUtils;
import com.github.tennaito.rsql.jpa.JpaPredicateVisitor;
import cz.jirutka.rsql.parser.RSQLParser;
import cz.jirutka.rsql.parser.ast.Node;
import cz.jirutka.rsql.parser.ast.RSQLVisitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
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
    @Autowired
    private EntityManager entityManager;

    @Autowired
    public PaymentService(PaymentConverter paymentConverter, PaymentRepository paymentRepository, RestTemplate restTemplate, UrlUtils urlUtils, KafkaService kafkaService) {
        this.paymentConverter = paymentConverter;
        this.paymentRepository = paymentRepository;
        this.restTemplate = restTemplate;
        this.urlUtils = urlUtils;
        this.kafkaService = kafkaService;
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

    public void processPayment(TransactionDto transactionDto, Payment payment,
                               KafkaPaymentNotificationDto kafkaNotification) {
        ExternalPaymentResponse externalPaymentResponse = sendPaymentToExternalPaymentSystem(transactionDto, payment);
        if (externalPaymentResponse.hasException()) {
            logger.warn("Payment failed via external service, updating payment status to failed, payment id - "
                    + payment.getId());
            payment.setPaymentStatus(PaymentStatus.FAILED);
            paymentRepository.save(payment);
            kafkaNotification.setPaymentInfo("payment failed via card number - " + transactionDto.getCardNumber());
            kafkaService.sendErrorMessage(kafkaNotification);
            throw new ExternalPaymentSystemException("Error received from external payment service: " +
                    externalPaymentResponse.getResponseBody());
        }

        logger.info("Payment done via external service, updating payment status to paid, payment id - "
                + payment.getId());
        payment.setPaymentStatus(PaymentStatus.PAID);
        paymentRepository.save(payment);
        kafkaNotification.setPaymentInfo("payment done via card number - " + transactionDto.getCardNumber());
        kafkaService.sendSuccessMessage(kafkaNotification);
    }

    public Payment createPayment(PaymentDto paymentDto, List<TicketInfoDto> ticketList, String userId) {
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


    public Optional<Payment> getPaymentById(Long id) {
        return paymentRepository.findById(id);
    }

    public List<Payment> findPayments(PaymentFilter filter) {
        Specification<Payment> spec = Specification.where(null);

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

        return paymentRepository.findAll(spec);
    }

}
