package eu.senla.aviasales.service;

import eu.senla.aviasales.entity.EmailNotification;
import eu.senla.aviasales.exception.ExceptionMessageConstants;
import eu.senla.aviasales.exception.custom.NotFoundException;
import eu.senla.aviasales.repository.EmailNotificationRepository;
import eu.senla.aviasales.response.ListEmailNotification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class CrudNotificationService {

    private final EmailNotificationRepository repository;

    public EmailNotification save(EmailNotification emailNotification) {
        log.info("... method save");
        return repository.save(emailNotification);
    }

    public void delete(EmailNotification emailNotification) {
        log.info("... method delete");
        repository.delete(emailNotification);
    }

    public ListEmailNotification findAll() {
        log.info("... method findAll");
        List<EmailNotification> emailNotifications = repository.findAll();
        return ListEmailNotification.builder()
                .emailNotificationList(emailNotifications)
                .count(emailNotifications.stream().count())
                .build();
    }

    public EmailNotification findById(String id) {
        log.info("... method findById");
        return repository.findById(id)
                .orElseThrow(() -> new NotFoundException(ExceptionMessageConstants.NOTIFICATION_NOT_FOUND));
    }

    public EmailNotification deleteById(String id) {
        log.info("... method deleteById");
        EmailNotification emailNotification = findById(id);
        repository.deleteById(id);
        return emailNotification;
    }
}
