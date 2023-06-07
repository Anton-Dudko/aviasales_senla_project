package com.aviasales.finance.service.external;

import com.aviasales.finance.dto.FlightDto;
import com.aviasales.finance.exception.TripServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TripService {
    private static final Logger logger = LoggerFactory.getLogger(TicketService.class);
    private final RestTemplate restTemplate;

    private final String tripServiceUrl = "http://trip-service:8081/flights";

    @Autowired
    public TripService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }


    public List<FlightDto> getFlights(List<Long> flightsList) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(tripServiceUrl)
                .pathSegment("findByIds").queryParam("ids", flightsList
                        .stream().map(Object::toString).collect(Collectors.joining(",")));

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


    public void checkTripDate(List<Long> flightsList) {
        logger.info("Check flight departure date");
        LocalDateTime latestTimeAvailableForReturn = LocalDateTime.now().plusHours(4);
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
        logger.info("Check flight date not in the past and min 1 hour before departure ");
        List<FlightDto> flightDtoList = getFlights(flightsList);
        flightDtoList = flightDtoList.stream().filter(x -> x.getDepartureDateTime().isBefore(LocalDateTime.now().plusHours(1)))
                .collect(Collectors.toList());
        if (flightDtoList.size() > 0) {
            logger.error("Some flights departure date in the past or around 1 hour (flights ids) " + flightDtoList.stream()
                    .map(FlightDto::getId).toList());
            throw new TripServiceException("Some flights departure date in the past or around 1 hour (flights ids):"
                    + flightDtoList.stream());
        }
    }
}
