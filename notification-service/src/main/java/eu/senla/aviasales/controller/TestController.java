package eu.senla.aviasales.controller;

import static eu.senla.aviasales.model.constant.Topic.PAYMENT_ERROR_EVENT;
import static eu.senla.aviasales.model.constant.Topic.PAYMENT_SUCCESS_EVENT;
import eu.senla.aviasales.model.dto.PaymentErrorEventDto;
import eu.senla.aviasales.model.dto.PaymentSuccessEventDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Mikhail.Leonovets
 * @since 05/2023
 */
// TODO TO BE DELETED!

@RequiredArgsConstructor
@RequestMapping("/api")
@RestController
public class TestController {
    private final KafkaTemplate<String, PaymentSuccessEventDto> paymentSuccessEventDtoKafkaTemplate;
    private final KafkaTemplate<String, PaymentErrorEventDto> paymentErrorEventDtoKafkaTemplate;

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PostMapping("/payment-success")
    public void testPaymentSuccess(@RequestBody PaymentSuccessEventDto dto) {
        paymentSuccessEventDtoKafkaTemplate.send(PAYMENT_SUCCESS_EVENT, dto);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PostMapping("/payment-error")
    public void testPaymentError(@RequestBody PaymentErrorEventDto dto) {
        paymentErrorEventDtoKafkaTemplate.send(PAYMENT_ERROR_EVENT, dto);
    }

}
