package eu.senla.aviasales.service.impl;

import eu.senla.aviasales.exception.custom.EmailSentNotFoundException;
import eu.senla.aviasales.model.entity.EmailSent;
import eu.senla.aviasales.repository.EmailSentRepository;
import eu.senla.aviasales.service.EmailSentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * @author Mikhail.Leonovets
 * @since 05/2023
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class EmailSentServiceImpl implements EmailSentService {
    private final EmailSentRepository emailSentRepository;

    @Override
    public EmailSent save(EmailSent emailSent) {
        return emailSentRepository.save(emailSent);
    }

    @Override
    public EmailSent findById(final Long id) throws EmailSentNotFoundException {
        return emailSentRepository.findById(id).orElseThrow(
                () -> new EmailSentNotFoundException(String.format("Email sent not found for id %s", id))
        );
    }

    @Override
    public List<EmailSent> findAllByCriteria(Date startDate, Date endDate, List<String> templateType,
                                             String email, int limit, int page) {
        if (startDate == null) {
            startDate = new Date(0);
        }
        if (endDate == null) {
            endDate = new Date();
        }
        return emailSentRepository.findByCriteria(startDate, endDate, templateType,
                email, PageRequest.of(page, limit));
    }

    @Override
    public EmailSent update(EmailSent emailSent) {
        return emailSentRepository.save(emailSent);
    }

    @Override
    public void deleteById(Long id) {
        emailSentRepository.deleteById(id);
    }
}
