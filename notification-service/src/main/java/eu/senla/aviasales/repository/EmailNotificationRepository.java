package eu.senla.aviasales.repository;

import eu.senla.aviasales.model.entity.EmailNotification;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmailNotificationRepository extends MongoRepository<EmailNotification, String> {

}
