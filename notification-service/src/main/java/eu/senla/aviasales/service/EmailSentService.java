package eu.senla.aviasales.service;

import eu.senla.aviasales.exception.custom.EmailSentNotFoundException;
import eu.senla.aviasales.model.entity.EmailSent;

import java.util.Date;
import java.util.List;

/**
 * @author Mikhail.Leonovets
 * @since 05/2023
 */
public interface EmailSentService {

    EmailSent save(EmailSent emailSent);

    EmailSent findById(Long id) throws EmailSentNotFoundException;

    List<EmailSent> findAllByCriteria(Date startDate, Date endDate, List<String> templateType, String email, int limit, int page);

    EmailSent update(EmailSent emailSent);

    void deleteById(Long id);

}
