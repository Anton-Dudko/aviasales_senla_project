package eu.senla.tripservice.service;

import eu.senla.tripservice.entity.Airplane;
import eu.senla.tripservice.entity.Flight;
import eu.senla.tripservice.entity.Trip;
import eu.senla.tripservice.exeption.flight.FlightAlreadyExistsException;
import eu.senla.tripservice.exeption.flight.FlightNotFoundException;
import eu.senla.tripservice.exeption.ticket.TicketsNotCreatedException;
import eu.senla.tripservice.mapper.Mapper;
import eu.senla.tripservice.repository.FlightRepository;
import eu.senla.tripservice.request.FindFlightRequest;
import eu.senla.tripservice.request.FlightRequest;
import eu.senla.tripservice.request.TicketsCreateRequest;
import eu.senla.tripservice.response.ticket.TicketsResponse;
import eu.senla.tripservice.response.flight.*;
import eu.senla.tripservice.util.time.TimeFormatter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional(readOnly = true)
public class FlightService {
    private final FlightRepository flightRepository;
    private final TripService tripService;
    private final AirplaneService airplaneService;
    private final Mapper mapper;

    @Autowired
    public FlightService(FlightRepository flightRepository, TripService tripService, AirplaneService airplaneService, Mapper mapper) {
        this.flightRepository = flightRepository;
        this.tripService = tripService;
        this.airplaneService = airplaneService;
        this.mapper = mapper;
    }

    @Transactional
    public void create(FlightRequest flightRequest) {
        log.info("FlightService-create");

        Trip trip = tripService.findTripById(flightRequest.getTripId());
        Airplane airplane = airplaneService.findById(flightRequest.getAirplaneId());
        Flight flightToSave = mapper.mapFlightRequestToFlight(flightRequest, trip, airplane);

        if (isFlightExist(flightToSave)) {
            throw new FlightAlreadyExistsException("The same flight already exists");
        }

        flightRepository.save(flightToSave);
        log.info("New flight was added, id: " + flightToSave.getFlightId());
        makeCreateTicketsRequest(generateTickets(flightToSave.getFlightId()));
    }

    public FlightFullDataResponse findById(long id) {
        return mapper.mapFlightToFlightFullDataResponse(findFlightById(id));
    }

    private Flight findFlightById(long id) {
        log.info("FlightService-findById: " + id);
        return flightRepository.findById(id)
                .orElseThrow(() -> new FlightNotFoundException("Flight with id = " + id + " not found"));
    }

    public ListFlightsFullDataResponse findAll(PageRequest pageRequest) {
        log.info("FlightService-findAll");
        ListFlightsFullDataResponse listFlightsFullDataResponse = new ListFlightsFullDataResponse();
        listFlightsFullDataResponse.setTripResponseList(flightRepository.findAll(pageRequest)
                .stream()
                .map(mapper::mapFlightToFlightFullDataResponse)
                .collect(Collectors.toList()));
        listFlightsFullDataResponse.setTotal(flightRepository.count());
        return listFlightsFullDataResponse;
    }

    public ListFlightsResponse find(FindFlightRequest findFlightRequest) {
        log.info("FlightService-find");
        Trip trip = tripService.findByDepartureCityAndArrivalCity(findFlightRequest.getDepartureCity(),
                findFlightRequest.getArrivalCity());

        LocalDateTime localDateTimeFrom = TimeFormatter.formatStringToDateTime(findFlightRequest.getDateFrom());
        LocalDateTime localDateTimeTo = TimeFormatter.formatStringToDateTime(findFlightRequest.getDateTo());

        List<FlightResponse> flightList = flightRepository.findAllByTripAndDepartureDateTimeBetween(trip, localDateTimeFrom, localDateTimeTo)
                .stream()
                .map(mapper::mapFlightToFlightResponse)
                .collect(Collectors.toList());

        ListFlightsResponse listFlightsResponse = new ListFlightsResponse();
        listFlightsResponse.setFlights(flightList);
        listFlightsResponse.setTotal(flightList.size());

        return listFlightsResponse;
    }

    @Transactional
    public void update(long id, FlightRequest flightRequest) {
        log.info("FlightService-update");
        findFlightById(id);
        Trip trip = tripService.findTripById(flightRequest.getTripId());
        Airplane airplane = airplaneService.findById(flightRequest.getAirplaneId());
        Flight updatedFlight = mapper.mapFlightRequestToFlight(flightRequest, trip, airplane);
        updatedFlight.setFlightId(id);
        flightRepository.save(updatedFlight);
        log.info("Flight with id: " + id + " was updated");
    }

    @Transactional
    public void delete(long id) {
        log.info("FlightService-delete, id: " + id);
        findFlightById(id);
        flightRepository.deleteById(id);
        log.info("Flight id: " + id + " was deleted");
    }

    public FlightInfo info(long id) {
        FlightInfo flightInfo = mapper.mapFlightToFlightInfo(findFlightById(id));
        flightInfo.setTickets(makeGetTicketsRequest(id).getTickets());
        return flightInfo;
    }

    private void makeCreateTicketsRequest(TicketsCreateRequest ticketsCreateRequest) {
        log.info("FlightService-makeCreateTicketsRequest");
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<TicketsCreateRequest> request = new HttpEntity<>(ticketsCreateRequest, headers);
        String createTicketsRequestUrl = "https://ticketservice/";

        ResponseEntity response = restTemplate.postForObject(createTicketsRequestUrl, request, ResponseEntity.class);

        if (Objects.requireNonNull(response).getStatusCodeValue() != HttpStatus.OK.value()) {
            throw new TicketsNotCreatedException(response.getStatusCodeValue() + "Ticket not created");
        }

    }

    private TicketsResponse makeGetTicketsRequest(long id) {
        log.info("Flight-service-makeGetTicketsRequest");
        RestTemplate restTemplate = new RestTemplate();

        String getTicketsRequestUrl = "https://ticketservice/" + id;

        return restTemplate.getForObject(getTicketsRequestUrl, TicketsResponse.class);
    }

    private TicketsCreateRequest generateTickets(long flightId) {
        log.info("FlightService-generateTickets, flight ID: " + flightId);
        Flight flight = findFlightById(flightId);
        Airplane airplane = flight.getAirplane();
        int seatsNumber = airplane.getNumberOfSeats();
        int firstClassSeatsNumber = (int) Math.round(seatsNumber / 3.0);
        int secondClassSeatsNumber = seatsNumber - firstClassSeatsNumber;
        double firstClassPrice = (airplane.getNumberOfSeats() * 4 + airplane.getAirplaneId()) / 2.0;
        double secondClassPrice = firstClassPrice * 0.8;

        return new TicketsCreateRequest(flightId, firstClassSeatsNumber, secondClassSeatsNumber, firstClassPrice, secondClassPrice);
    }

    private boolean isFlightExist(Flight flightToCheck) {
        return flightRepository.findFlightByTrip_TripIdAndAirplane_AirplaneIdAndFlightNumberAndDepartureDateTimeAndArrivalDateTime(
                flightToCheck.getTrip().getTripId(), flightToCheck.getAirplane().getAirplaneId(), flightToCheck.getFlightNumber(),
                flightToCheck.getDepartureDateTime(), flightToCheck.getArrivalDateTime()).isPresent();
    }
}
