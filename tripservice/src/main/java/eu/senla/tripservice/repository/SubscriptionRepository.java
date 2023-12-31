package eu.senla.tripservice.repository;

import eu.senla.tripservice.entity.Subscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {
    Optional<Subscription> findByEventNameAndUserIdAndTripFlightId(String eventName,
                                                                   long userId,
                                                                   long tripFlightId);

    List<Subscription> findAllByEventNameAndTripFlightId(String eventName,
                                                         long tripFlightId);
}
