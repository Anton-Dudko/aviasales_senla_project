package eu.senla.aviasales.service;

import eu.senla.aviasales.entity.EmailNotification;
import eu.senla.aviasales.exception.ExceptionMessageConstants;
import eu.senla.aviasales.exception.custom.NotFoundException;
import eu.senla.aviasales.repository.EmailNotificationRepository;
import eu.senla.aviasales.response.ListEmailNotification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
@Validated
public class CrudNotificationService {

    private final EmailNotificationRepository repository;

    public EmailNotification save(@Valid EmailNotification emailNotification,
                                  @NotNull String exceptionMessage) {
        log.info("... method save");
        if (emailNotification.getDateFirstSend() == null) {
            emailNotification.setDateFirstSend(LocalDate.now());
        }
        if (emailNotification.getCountTrySend() == null) {
            emailNotification.setCountTrySend(0);
        }
        if (emailNotification.getIsInSendingProcess() == null) {
            emailNotification.setIsInSendingProcess(true);
        }
        emailNotification.setException(exceptionMessage);
        return repository.save(emailNotification);
    }

    public void delete(@Valid EmailNotification emailNotification) {
        log.info("... method delete");
        repository.delete(emailNotification);
    }

    public ListEmailNotification findAll() {
        log.info("... method findAll");
        List<EmailNotification> emailNotifications = repository.findAll();
        return ListEmailNotification.builder()
                .emailNotificationList(emailNotifications)
                .count((long) emailNotifications.size())
                .build();
    }

    public EmailNotification findById(@NotNull String id) {
        log.info("... method findById");
        return repository.findById(id)
                .orElseThrow(() -> new NotFoundException(ExceptionMessageConstants.NOTIFICATION_NOT_FOUND));
    }

    public EmailNotification deleteById(@NotNull String id) {
        log.info("... method deleteById");
        EmailNotification emailNotification = findById(id);
        repository.deleteById(id);
        return emailNotification;
    }

    public List<EmailNotification> findAllInProgress() {
        log.info("... method findAllInProgress");
        return repository.findAllByIsInSendingProcessTrue();
    }
}
