package com.aviasalestickets.service.api;

import com.aviasalestickets.exception.TripRestTemplateException;
import com.aviasalestickets.model.dto.trip.FlightInfoDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class TripApi {

    private final RestTemplate restTemplate;

    @Transactional
    public FlightInfoDto requestToTrip(Long id) {
        FlightInfoDto flight;
        try {
            flight = restTemplate.getForObject("/flights/admin/find/{id}", FlightInfoDto.class, id);//TODO test
        } catch (HttpClientErrorException e) {
            throw new TripRestTemplateException(e.getMessage());
        }
        return flight;
    }
}
