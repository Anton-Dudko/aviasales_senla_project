package eu.senla.aviasales.service;

import eu.senla.aviasales.exception.custom.EmailSentNotFoundException;
import eu.senla.aviasales.model.dto.CustomEmailDto;

/**
 * @author Mikhail.Leonovets
 * @since 05/2023
 */
public interface AdminEmailService {

    void sendCustomEmail(CustomEmailDto customEmailDto);

    void sendAgain(Long id) throws EmailSentNotFoundException;

}
