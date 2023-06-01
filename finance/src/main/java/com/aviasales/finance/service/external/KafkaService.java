package com.aviasales.finance.service.external;

import com.aviasales.finance.dto.KafkaPaymentNotificationDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

@Service
public class KafkaService {

    private static final Logger logger = LoggerFactory.getLogger(KafkaService.class);
    private final KafkaTemplate<String, Object> kafkaTemplate;
    @Value("${finance.kafka.notification.topic.success}")
    private String topicSuccess;
    @Value("${finance.kafka.notification.topic.error}")
    private String topicError;

    @Autowired
    public KafkaService(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Async
    public void sendErrorMessage(KafkaPaymentNotificationDto paymentNotificationDto) {
        logger.warn("Send error message to KAFKA");
        sendMessage(topicError, paymentNotificationDto);
    }

    @Async
    public void sendSuccessMessage(KafkaPaymentNotificationDto paymentNotificationDto) {
        logger.info("Send success message to KAFKA");
        sendMessage(topicSuccess, paymentNotificationDto);
    }

//    public void send(String topic, String  message) {
//        ListenableFuture<SendResult<String, Object>> future = kafkaTemplate.send(topic, message);
//        future.addCallback(new ListenableFutureCallback<SendResult<String, Object>>() {
//
//            @Override
//            public void onSuccess(final SendResult<String, String> message) {
//                logger.info("sent message= " + message + " with offset= " + message.getRecordMetadata().offset());
//            }
//
//            @Override
//            public void onFailure(final Throwable throwable) {
//                logger.error("unable to send message= " + message, throwable);
//            }
//        });
//    }

    public void sendMessage(String topic, KafkaPaymentNotificationDto message) {
        kafkaTemplate.send(topic, message);
    }
}
