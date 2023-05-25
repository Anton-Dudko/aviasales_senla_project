package eu.senla.tripservice.service;

import eu.senla.tripservice.entity.Subscription;
import eu.senla.tripservice.exeption.subscription.SubscriptionAlreadyExistsException;
import eu.senla.tripservice.mapper.Mapper;
import eu.senla.tripservice.repository.SubscriptionRepository;
import eu.senla.tripservice.request.SubscriptionRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class SubscriptionService {
    private final SubscriptionRepository subscriptionRepository;
    private final Mapper mapper;

    @Autowired
    public SubscriptionService(SubscriptionRepository subscriptionRepository, Mapper mapper) {
        this.subscriptionRepository = subscriptionRepository;
        this.mapper = mapper;
    }

    public void save(SubscriptionRequest request) {
        log.info("SubscriptionService-save");
        Subscription subscriptionToSave = mapper.mapSubscriptionRequestToSubscription(request);

        if (isSubscriptionExist(subscriptionToSave)) {
            throw new SubscriptionAlreadyExistsException("The same subscription already exists");
        }

        subscriptionRepository.save(subscriptionToSave);
        log.info("New subscription was added");
    }

    private boolean isSubscriptionExist(Subscription subscription) {
        return subscriptionRepository.findByEventNameAndUserIdAndTripFlightId(subscription.getEventName(),
                subscription.getUserId(), subscription.getTripFlightId()).isPresent();
    }
}
