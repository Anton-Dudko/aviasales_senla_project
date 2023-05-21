package eu.senla.tripservice.service;

import eu.senla.tripservice.entity.Flight;
import eu.senla.tripservice.exeption.flight.FlightNotCreatedException;
import eu.senla.tripservice.repository.FlightRepository;
import eu.senla.tripservice.request.FlightRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

@Slf4j
@Service
@Transactional(readOnly = true)
public class FlightService {
    private final FlightRepository flightRepository;
    private final TripService tripService;
    private final AirplaneService airplaneService;

    public FlightService(FlightRepository flightRepository, TripService tripService, AirplaneService airplaneService) {
        this.flightRepository = flightRepository;
        this.tripService = tripService;
        this.airplaneService = airplaneService;
    }

    @Transactional
    public void save(FlightRequest flightRequest) {
        log.info("FlightService-save");
        Flight flightToSave = convertToFlight(flightRequest);
        flightRepository.save(flightToSave);
        log.info("New flight was added, id: " + flightToSave.getFlightId());
    }

    private Flight convertToFlight(FlightRequest flightRequest) {
        return Flight.builder()
                .trip(tripService.findTripById(flightRequest.getTripId()))
                .airplane(airplaneService.findById(flightRequest.getAirplaneId()))
                .flightNumber(flightRequest.getFlightNumber())
                .departureDateTime(formatStringDateTime(flightRequest.getDepartureDateTime()))
                .arrivalDateTime(formatStringDateTime(flightRequest.getArrivalDateTime()))
                .meal(flightRequest.isMeal())
                .handLuggage(flightRequest.isHandLuggage())
                .luggage(flightRequest.isLuggage())
                .build();
    }

    private LocalDateTime formatStringDateTime(String dateTime) {
        String pattern = "dd-MM-yyyy HH:mm";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
        LocalDateTime localDateTime;
        try {
            localDateTime = LocalDateTime.parse(dateTime, formatter);
        } catch (DateTimeParseException exception) {
            throw new FlightNotCreatedException("Date and time must match the pattern: " + pattern);
        }
        return localDateTime;
    }
}
