package eu.senla.aviasales.repository;

import eu.senla.aviasales.entity.EmailNotification;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmailNotificationRepository extends MongoRepository<EmailNotification, String> {

    List<EmailNotification> findAllByIsInSendingProcessTrue();

}
