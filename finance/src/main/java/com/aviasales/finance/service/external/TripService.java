package com.aviasales.finance.service.external;

import com.aviasales.finance.dto.FlightDto;
import com.aviasales.finance.dto.TicketInfoDto;
import com.aviasales.finance.exception.TripServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
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

    public void checkTripDate(List<Long> flightsList) {
        logger.info("Check flight departure date");
        LocalDateTime nearestFlightTime = null;


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

            List<FlightDto> flightDtoList = response.getBody();

            nearestFlightTime = flightDtoList.stream().map(FlightDto::getDepartureDateTime).min(LocalDateTime::compareTo)
                    .orElseThrow(() ->
                            new TripServiceException("There are no date info for flights"));

            LocalDateTime latestTimeAvailableForReturn = LocalDateTime.now().plusHours(4);
            if (nearestFlightTime.isBefore(latestTimeAvailableForReturn)) {
                logger.warn("Payment can not be returned, because some flight are 4 hours before departure");
                throw new TripServiceException("Payment can not be returned, because some flight are 4 hours before departure");
            }
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            logger.error("Error received from trip service during getting flight time : " + e.getMessage(), e);
            throw new TripServiceException("Error received from trip service during getting flight time : " +
                    e.getMessage());
        }
    }
}
