package com.aviasalestickets.service.api;

import com.aviasalestickets.model.dto.trip.FlightInfoDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class TripApi {

    private final RestTemplate restTemplate;
    public FlightInfoDto requestToTrip(Long id){
        String url = "http://trip-service:8081/flights/admin/find/{id}"; //trip-service:8081    localhost:8081
        FlightInfoDto flight = restTemplate.getForObject(url, FlightInfoDto.class, id);
        return flight;
    }
}
