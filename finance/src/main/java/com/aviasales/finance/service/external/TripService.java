package com.aviasales.finance.service.external;

import com.aviasales.finance.dto.flight.FlightDto;
import com.aviasales.finance.exception.TripServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TripService {
    private static final Logger logger = LoggerFactory.getLogger(TicketService.class);
    private final RestTemplate restTemplate;
    @Value("${trip.service.url}")
    private String tripServiceUrl;

    @Value("${ticket.min.time.before.departure.refund}")
    private int minHourBeforeDepartureForRefund;

    @Autowired
    public TripService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public List<FlightDto> getFlights(List<Long> flightsList) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(tripServiceUrl)
                .pathSegment("findByIds").queryParam("ids", flightsList
                        .stream().map(Object::toString).collect(Collectors.joining(",")));

        logger.info("trying to check flights " + builder.toUriString());
        try {
            ResponseEntity<List<FlightDto>> response = restTemplate.exchange(builder.toUriString(), HttpMethod.GET,
                    new HttpEntity<>(new HttpHeaders()), new ParameterizedTypeReference<List<FlightDto>>() {
                    });

            if (response.getBody() == null || response.getBody().isEmpty()) {
                throw new TripServiceException("There are no such flights, empty body returned from flight service: ");
            }

            return response.getBody();
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            logger.error("Error received from trip service during getting flight time : " + e.getMessage(), e);
            throw new TripServiceException("Error received from trip service during getting flight time : " +
                    e.getMessage());
        }
    }

    public void checkTripDateForRefund(List<Long> flightsList) {
        logger.info("Check flight departure date");
        LocalDateTime latestTimeAvailableForReturn = LocalDateTime.now().plusHours(minHourBeforeDepartureForRefund);
        List<FlightDto> flightDtoList = getFlights(flightsList);

        LocalDateTime nearestFlightTime = flightDtoList.stream().map(FlightDto::getDepartureDateTime).min(LocalDateTime::compareTo)
                .orElseThrow(() ->
                        new TripServiceException("There are no date info for flights"));

        if (nearestFlightTime.isBefore(latestTimeAvailableForReturn)) {
            logger.warn("Payment can not be returned, because some flight are 4 hours before departure");
            throw new TripServiceException("Payment can not be returned, because some flight are 4 hours before departure");
        }
    }

    public void checkFlightDateForPaying(List<Long> flightsList) {
        logger.info("Check flight date not in the past");

        List<FlightDto> flightDtoList = getFlights(flightsList);
        flightDtoList = flightDtoList.stream().filter(x -> x.getDepartureDateTime().isBefore(LocalDateTime.now()))
                .collect(Collectors.toList());

        if (flightDtoList.size() > 0) {
            String flightsIds = flightDtoList.stream()
                    .map(FlightDto::getFlightId).map(String::valueOf).collect(Collectors.joining(","));
            logger.info("debug check flights - " + flightDtoList.stream().map(FlightDto::getFlightId).toList());
            logger.error("Some flights departure date in the past (flights ids) " + flightsIds);
            throw new TripServiceException("Some flights departure date in the past (flights ids):"
                    + flightsIds);
        }
    }
}
