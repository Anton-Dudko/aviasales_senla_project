package eu.senla.aviasales.service.impl;

import eu.senla.aviasales.exception.custom.EmailSentNotFoundException;
import eu.senla.aviasales.model.dto.CustomEmailDto;
import eu.senla.aviasales.service.AdminEmailService;
import eu.senla.aviasales.service.EmailSentService;
import eu.senla.aviasales.service.MessageBuilderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author Mikhail.Leonovets
 * @since 05/2023
 */
@RequiredArgsConstructor
@Slf4j
@Service
public class AdminEmailServiceImpl implements AdminEmailService {
    private final EmailSentService emailSentService;
    private final MessageBuilderService messageBuilderService;

    private final KafkaService kafkaService;
    @Override
    public void sendCustomEmail(final CustomEmailDto customEmailDto) {
        kafkaService.send(customEmailDto);
    }

    @Override
    public void sendAgain(final Long id) throws EmailSentNotFoundException {
        messageBuilderService.buildAndSend(emailSentService.findById(id));
    }
}
