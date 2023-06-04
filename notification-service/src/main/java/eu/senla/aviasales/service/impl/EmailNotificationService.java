package eu.senla.aviasales.service.impl;

import eu.senla.aviasales.model.entity.EmailNotification;
import eu.senla.aviasales.repository.EmailNotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class EmailNotificationService {

    private final EmailNotificationRepository repository;

    public EmailNotification save(EmailNotification emailNotification) {
        return repository.save(emailNotification);
    }
}
