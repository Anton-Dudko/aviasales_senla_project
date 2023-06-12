package eu.senla.aviasales.service;

import eu.senla.aviasales.entity.EmailNotification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;

@RequiredArgsConstructor
@Slf4j
@Service
public class ScheduledService {
    @Value("${email.send.count}")
    private Integer maxCountSending;
    private final CrudNotificationService crudNotificationService;
    private final SendService sendService;

    @Scheduled(fixedDelayString = "${email.send.interval}")
    public void sendWrongLetter() {
        log.info("... method sendWrongLetter");
        List<EmailNotification> emailNotifications = crudNotificationService.findAllInProgress();
        if (!CollectionUtils.isEmpty(emailNotifications)) {
            emailNotifications.forEach(notification -> {
                if (isLessThenMaxCountSending(notification)) {
                    sendService.sendEmail(notification);
                }
            });
        }
    }

    private boolean isLessThenMaxCountSending(EmailNotification e) {
        log.info("... method isLessMaxCountSending");
        if (e.getCountTrySend() >= maxCountSending) {
            e.setIsInSendingProcess(false);
            crudNotificationService.save(e, e.getException());
            return false;
        }
        return true;
    }
}
