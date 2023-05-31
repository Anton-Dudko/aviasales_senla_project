package eu.senla.tripservice.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import eu.senla.tripservice.entity.Subscription;
import eu.senla.tripservice.entity.Trip;
import eu.senla.tripservice.exeption.ticket.RequestException;
import eu.senla.tripservice.request.UserDetails;
import eu.senla.tripservice.response.flight.KafkaFlightDTO;
import eu.senla.tripservice.response.ticket.TicketsResponse;
import eu.senla.tripservice.util.KafkaTopicsName;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class KafkaService {
    private final KafkaTemplate<String, Map<String, KafkaFlightDTO>> kafkaTemplate;
    private final ObjectMapper objectMapper;
    private final SubscriptionService subscriptionService;
    private final FlightService flightService;
    private final TripService tripService;

    @Autowired
    public KafkaService(KafkaTemplate<String, Map<String, KafkaFlightDTO>> kafkaTemplate,
                        ObjectMapper objectMapper, @Lazy SubscriptionService subscriptionService,
                        @Lazy FlightService flightService, @Lazy TripService tripService) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
        this.subscriptionService = subscriptionService;
        this.flightService = flightService;
        this.tripService = tripService;
    }

    public void newEvent(String eventName, long flightId, long tripId, LocalDateTime departureDate) {
        log.info("KafkaService-newEvent");
        List<Subscription> subscriptions = new ArrayList<>();
        switch (eventName) {
            case (KafkaTopicsName.NEW_FLIGHT_EVENT) -> {
                log.info("KafkaService-newEvent-case: " + KafkaTopicsName.NEW_FLIGHT_EVENT);
                subscriptions = subscriptionService.findSubscription(eventName, tripId);
            }
            case (KafkaTopicsName.FLIGHT_CANCELED_EVENT) -> {
                log.info("KafkaService-newEvent-case: " + KafkaTopicsName.FLIGHT_CANCELED_EVENT);
                subscriptions = subscriptionService.findSubscription(eventName, flightId);
            }
        }

        if (!subscriptions.isEmpty()) {
            log.info("KafkaService-newEvent: subscription's count = " + subscriptions.size());
            for (Subscription subscription : subscriptions) {
                long userId = subscription.getUserId();
                KafkaFlightDTO kafkaFlightDTO = createKafkaFlightDTO(userId, flightId, tripId, departureDate);
                if (kafkaFlightDTO != null) {
                    sendKafkaNewEvent(eventName, kafkaFlightDTO);
                }
            }
        }
    }

    private KafkaFlightDTO createKafkaFlightDTO(long userId, long flightId, long tripId, LocalDateTime departureDate) {
        log.info("KafkaService-createKafkaFlightDTO");
        UserDetails user = makeRequestForUserDetails(userId);
        KafkaFlightDTO flightDTO = null;
        if (user != null && user.getLanguage() != null && user.getEmail() != null && user.getUsername() != null) {
            log.info("KafkaService-createKafkaFlightDTO: got response from user-service");
            TicketsResponse tickets = flightService.makeGetTicketsRequest(flightId);
            if (tickets.getTickets() != null || !tickets.getTickets().isEmpty()) {
                log.info("KafkaService-createKafkaFlightDTO: got response from ticket-service");
                flightService.sortTickets(tickets.getTickets());
                Trip trip = tripService.findTripById(tripId);
                flightDTO = new KafkaFlightDTO();
                flightDTO.setUserLanguage(user.getLanguage());
                flightDTO.setEmail(user.getEmail());
                flightDTO.setUserName(user.getUsername());
                flightDTO.setFrom(trip.getDepartureCity());
                flightDTO.setTo(trip.getArrivalCity());
                flightDTO.setTripDate(departureDate);
                flightDTO.setPrice(tickets.getTickets().get(0).getPrice());
            }
        }
        return flightDTO;
    }

    public void sendKafkaNewEvent(String eventName, KafkaFlightDTO flightDTO) {
        log.info("KafkaService-sentKafkaNewEvent");
        kafkaTemplate.send(eventName, objectMapper.convertValue(flightDTO, Map.class));
    }

    public UserDetails makeRequestForUserDetails(long userId) {
        log.info("KafkaService-makeRequestForUserDetails");
        RestTemplate restTemplate = new RestTemplate();
        String getUserDetailsUrl = "http://userservice:8086/admin/users/" + userId;
        UserDetails response;
        try {
            response = restTemplate.getForObject(getUserDetailsUrl, UserDetails.class);
        } catch (HttpClientErrorException e) {
            throw new RequestException(e.getResponseBodyAsString());
        }
        return response;
    }
}