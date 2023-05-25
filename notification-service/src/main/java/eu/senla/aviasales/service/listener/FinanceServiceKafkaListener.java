package eu.senla.aviasales.service.listener;

import static eu.senla.aviasales.model.constant.Topic.PAYMENT_ERROR_EVENT;
import static eu.senla.aviasales.model.constant.Topic.PAYMENT_SUCCESS_EVENT;
import eu.senla.aviasales.model.dto.PaymentErrorEventDto;
import eu.senla.aviasales.model.dto.PaymentSuccessEventDto;
import eu.senla.aviasales.model.dto.UserDataDto;
import eu.senla.aviasales.service.MessageBuilderService;
import eu.senla.aviasales.service.validator.ValidatorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;

/**
 * @author Mikhail.Leonovets
 * @since 05/2023
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class FinanceServiceKafkaListener {
    private final ValidatorService<UserDataDto> userDataDtoValidatorService;
    private final MessageBuilderService<PaymentSuccessEventDto> paymentSuccessEventDtoMessageBuilderService;
    private final MessageBuilderService<PaymentErrorEventDto> paymentErrorEventDtoMessageBuilderService;

    @KafkaListener(topicPattern = PAYMENT_SUCCESS_EVENT, autoStartup = "true")
    public void paymentSuccessListener(ConsumerRecord<String, PaymentSuccessEventDto> record) throws Exception {
        PaymentSuccessEventDto value = record.value();
        userDataDtoValidatorService.validate(value);
        try {
            paymentSuccessEventDtoMessageBuilderService.buildAndSend(value);
        } catch (MessagingException e) {
            log.warn(e.getMessage());
            throw new MessagingException(e.getMessage());
        }
    }

    @KafkaListener(topicPattern = PAYMENT_ERROR_EVENT, autoStartup = "true")
    public void paymentErrorListener(ConsumerRecord<String, PaymentErrorEventDto> record) throws Exception {
        PaymentErrorEventDto value = record.value();
        userDataDtoValidatorService.validate(value);
        try {
            paymentErrorEventDtoMessageBuilderService.buildAndSend(value);
        } catch (MessagingException e) {
            log.warn(e.getMessage());
            throw new MessagingException(e.getMessage());
        }
    }

}
