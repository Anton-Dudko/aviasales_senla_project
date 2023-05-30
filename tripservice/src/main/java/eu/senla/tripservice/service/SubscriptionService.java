package eu.senla.tripservice.service;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.databind.ObjectMapper;
import eu.senla.tripservice.entity.Subscription;
import eu.senla.tripservice.entity.Trip;
import eu.senla.tripservice.exeption.subscription.SubscriptionAlreadyExistsException;
import eu.senla.tripservice.exeption.subscription.SubscriptionException;
import eu.senla.tripservice.repository.SubscriptionRepository;
import eu.senla.tripservice.request.CanceledFlightSubscriptionRequest;
import eu.senla.tripservice.request.NewFlightsSubscriptionRequest;
import eu.senla.tripservice.request.UserDetails;
import eu.senla.tripservice.response.flight.FlightFullDataResponse;
import eu.senla.tripservice.util.KafkaTopicsName;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
public class SubscriptionService {
    private final SubscriptionRepository subscriptionRepository;
    private final TripService tripService;
    private final FlightService flightService;
    private final ObjectMapper objectMapper;

    @Autowired
    public SubscriptionService(SubscriptionRepository subscriptionRepository, TripService tripService,
                               FlightService flightService, ObjectMapper objectMapper) {
        this.subscriptionRepository = subscriptionRepository;
        this.tripService = tripService;
        this.flightService = flightService;
        this.objectMapper = objectMapper;
    }

    public Subscription createNewFlightSubscription(NewFlightsSubscriptionRequest request, String userDetails) {
        log.info("SubscriptionService-createNewFlightSubscription");
        long userId = getUserId(userDetails);
        Trip trip = tripService.findByDepartureCityAndArrivalCity(request.getDepartureCity(), request.getArrivalCity());
        return saveSubscription(KafkaTopicsName.NEW_FLIGHT_EVENT, userId, trip.getTripId());
    }

    public Subscription createCanceledFlightSubscription(CanceledFlightSubscriptionRequest request, String userDetails) {
        log.info("SubscriptionService-createCanceledFlightSubscription");
        long userId = getUserId(userDetails);
        FlightFullDataResponse flight = flightService.findById(request.getFlightId());
        return saveSubscription(KafkaTopicsName.FLIGHT_CANCELED_EVENT, userId, flight.getFlightId());
    }

    private Subscription saveSubscription(String eventName, long userId, long tripFlightId) {
        Subscription subscriptionToSave = Subscription.builder()
                .eventName(eventName)
                .userId(userId)
                .tripFlightId(tripFlightId)
                .build();

        if (isSubscriptionExist(subscriptionToSave)) {
            throw new SubscriptionAlreadyExistsException("The same subscription already exists");
        }

        subscriptionRepository.save(subscriptionToSave);
        log.info("New subscription was added");
        return subscriptionToSave;
    }

    public long findSubscription(String eventName, Long tripFlightId) {
        long userId = 0;
        Optional<Subscription> subscription = subscriptionRepository.findByEventNameAndTripFlightId(eventName, tripFlightId);
        if (subscription.isPresent())
            userId = subscription.get().getUserId();
        return userId;
    }

    private boolean isSubscriptionExist(Subscription subscription) {
        return subscriptionRepository.findByEventNameAndUserIdAndTripFlightId(subscription.getEventName(),
                subscription.getUserId(), subscription.getTripFlightId()).isPresent();
    }

    private long getUserId(String userDetails) {
        UserDetails user;
        long userId = 0;
        try {
            user = objectMapper.readValue(userDetails, UserDetails.class);
            if (user != null) {
                userId = user.getUserId();
            }
        } catch (JacksonException e) {
            throw new SubscriptionException(e.getOriginalMessage());
        }
        if (userId == 0) {
            throw new SubscriptionException("userId must be greater than 0");
        }
        return userId;
    }
}
