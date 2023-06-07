package eu.senla.aviasales.service;

import eu.senla.aviasales.entity.EmailNotification;
import eu.senla.aviasales.kafka.KafkaTopicConstants;
import eu.senla.aviasales.request.CustomEmailRequest;
import eu.senla.aviasales.response.UserResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AlarmAdminService {
    private final SendToKafkaService sendToKafkaService;
    private final UserserviseService userserviseService;

    public void sendTroublem(EmailNotification emailNotification, String error) {
        log.info("... method ALARMADMIN sendTroublem");
        List<UserResponse> adminList = userserviseService.getAllAdminEmail().getUserResponseList();
        if (!adminList.stream().map(UserResponse::getEmail).toList().contains(emailNotification.getReceiver())) {
            adminList.forEach(admin ->
                    sendToKafkaService.sendCustomEmail(KafkaTopicConstants.ERROR_EMAIL_TYPE, CustomEmailRequest.builder()
                            .userName(admin.getUsername())
                            .email(admin.getEmail())
                            .subject("ERROR send message ")
                            .body(emailNotification + "\n" + error)
                            .build())
            );
        }
    }
}

