package com.aviasales.finance.service;

import com.aviasales.finance.converter.PaymentConverter;
import com.aviasales.finance.dto.PaymentDto;
import com.aviasales.finance.dto.TicketInfoDto;
import com.aviasales.finance.dto.TransactionDto;
import com.aviasales.finance.entity.Payment;
import com.aviasales.finance.enums.PaymentStatus;
import com.aviasales.finance.exception.ExternalPaymentSystemException;
import com.aviasales.finance.repository.PaymentRepository;
import com.aviasales.finance.utils.UrlUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDateTime;

@Service
public class PaymentSystemService {
    private static final Logger logger = LoggerFactory.getLogger(PaymentSystemService.class);
    private final PaymentConverter paymentConverter;
    private final PaymentRepository paymentRepository;
    private final RestTemplate restTemplate;
    private final UrlUtils urlUtils;

    @Autowired
    public PaymentSystemService(PaymentConverter paymentConverter, PaymentRepository paymentRepository, RestTemplate restTemplate, UrlUtils urlUtils) {
        this.paymentConverter = paymentConverter;
        this.paymentRepository = paymentRepository;
        this.restTemplate = restTemplate;
        this.urlUtils = urlUtils;
    }

    public void sendPaymentToExternalPaymentSystem(PaymentDto paymentDto, TicketInfoDto ticketInfoDto) {
        Payment payment = paymentConverter.convertToEntity(paymentDto);
        payment.setPrice(ticketInfoDto.getPrice());
        payment.setPaymentCreationDateTime(LocalDateTime.now());
        payment.setPaymentStatus(PaymentStatus.PENDING);
        payment = paymentRepository.save(payment);

        String baseUrl = urlUtils.getRootUrl();
        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(baseUrl + "/external/payment");
        TransactionDto transactionDto = createTransaction(paymentDto, ticketInfoDto.getPrice());

        try {
            restTemplate.exchange(builder.toUriString(), HttpMethod.POST,
                    new HttpEntity<>(transactionDto, new HttpHeaders()), String.class);
            payment.setPaymentStatus(PaymentStatus.PAID);
            logger.info("Payment done via external service, updating payment status");
            paymentRepository.save(payment);

        } catch (HttpClientErrorException | HttpServerErrorException e) {
            String responseBody = e.getResponseBodyAsString();
            logger.error("Error received from external payment service: " + e.getMessage(), e);
            payment.setPaymentStatus(PaymentStatus.FAILED);
            paymentRepository.save(payment);
            throw new ExternalPaymentSystemException("Error received from external payment service: " + responseBody);
        }
    }

    public TransactionDto createTransaction(PaymentDto paymentDto, double sumToPay) {
        TransactionDto transactionDto = new TransactionDto();
        transactionDto.setCardNumber(paymentDto.getCardNumber());
        transactionDto.setCardHolder(paymentDto.getCardHolder());
        transactionDto.setCardDate(paymentDto.getCardDate());
        transactionDto.setCvv(paymentDto.getCvv());
        transactionDto.setSum(sumToPay);
        return transactionDto;
    }
}
