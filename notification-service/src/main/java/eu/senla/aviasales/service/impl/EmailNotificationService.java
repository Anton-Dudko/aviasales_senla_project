package eu.senla.aviasales.service.impl;

import eu.senla.aviasales.model.entity.EmailNotification;
import eu.senla.aviasales.repository.EmailNotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class EmailNotificationService {

    private final EmailNotificationRepository repository;

    public EmailNotification save(EmailNotification emailNotification) {
        log.info("... method save");
        return repository.save(emailNotification);
    }

    public List<EmailNotification> findAll() {
        log.info("... method findAll");
        return repository.findAll();
    }
}
