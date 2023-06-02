package eu.senla.aviasales.repository;

import eu.senla.aviasales.model.entity.EmailSent;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

/**
 * @author Mikhail.Leonovets
 * @since 05/2023
 */
@Repository
public interface EmailSentRepository extends JpaRepository<EmailSent, Long> {

    @Query("SELECT es FROM EmailSent es " +
            "WHERE :templateType IS NULL OR es.templateType IN :templateType " +
            "AND :receiver IS NULL OR es.receiver = :receiver " +
            "AND es.sentDate BETWEEN :startDate AND :endDate")
    List<EmailSent> findByCriteria(Date startDate, Date endDate, List<String> templateType,
                                   String receiver, Pageable pageable);

}
