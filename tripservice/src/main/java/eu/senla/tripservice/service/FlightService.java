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
import eu.senla.tripservice.response.flight.FlightFullDataResponse;
import eu.senla.tripservice.response.flight.FlightInfo;
import eu.senla.tripservice.response.flight.ListFlightsFullDataResponse;
import eu.senla.tripservice.response.flight.ListFlightsResponse;
import eu.senla.tripservice.response.ticket.TicketsResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
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
    public Flight create(FlightRequest flightRequest) {
        log.info("FlightService-create");

        Trip trip = tripService.findTripById(flightRequest.getTripId());
        Airplane airplane = airplaneService.findById(flightRequest.getAirplaneId());
        Flight flightToSave = mapper.mapFlightRequestToFlight(flightRequest, trip, airplane);

        if (isFlightExist(flightToSave)) {
            throw new FlightAlreadyExistsException("The same flight already exists");
        }

        flightRepository.save(flightToSave);
        log.info("New flight was added, id: " + flightToSave.getFlightId());
        makeCreateTicketsRequest(generateTickets(flightToSave.getFlightId(),
                flightRequest.getFirstClassTicketPercent(), flightRequest.getTicketPrice()));
        return flightToSave;
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

    public ListFlightsResponse find(FindFlightRequest findFlightRequest, Pageable pageable) {
        log.info("FlightService-find");

        Page<Flight> flights = flightRepository.findAll(new FlightSpecification(findFlightRequest), pageable);
        ListFlightsResponse listFlightsResponse = new ListFlightsResponse();

        if (flights.hasContent()) {
            listFlightsResponse.setFlights(flights.getContent().stream().map(mapper::mapFlightToFlightResponse).collect(Collectors.toList()));
            listFlightsResponse.setTotal(flights.getContent().size());
        } else {
            listFlightsResponse.setFlights(new ArrayList<>());
            listFlightsResponse.setTotal(0);
        }
        return listFlightsResponse;
    }

    @Transactional
    public Flight update(long id, FlightRequest flightRequest) {
        log.info("FlightService-update");
        findFlightById(id);
        Trip trip = tripService.findTripById(flightRequest.getTripId());
        Airplane airplane = airplaneService.findById(flightRequest.getAirplaneId());
        Flight updatedFlight = mapper.mapFlightRequestToFlight(flightRequest, trip, airplane);
        updatedFlight.setFlightId(id);
        flightRepository.save(updatedFlight);
        log.info("Flight with id: " + id + " was updated");
        return updatedFlight;
    }

    @Transactional
    public void delete(long id) {
        log.info("FlightService-delete, id: " + id);
        findFlightById(id);
        flightRepository.deleteById(id);
        log.info("Flight id: " + id + " was deleted");
    }

    public FlightInfo info(long id) {
        log.info("FlightService-info, id:" + id);
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
        String createTicketsRequestUrl = "http://ticket-service:8080/tickets/generate";

        try {
            restTemplate.postForObject(createTicketsRequestUrl, request, ResponseEntity.class);
        } catch (Exception e) {
            throw new TicketsNotCreatedException("Ticket not created: " + e.getMessage());
        }

    }

    private TicketsResponse makeGetTicketsRequest(long id) {
        log.info("Flight-service-makeGetTicketsRequest");
        RestTemplate restTemplate = new RestTemplate();

        String getTicketsRequestUrl = "https://ticket-service/" + id;

        return restTemplate.getForObject(getTicketsRequestUrl, TicketsResponse.class);
    }

    private TicketsCreateRequest generateTickets(long flightId, int ticketPercent, double ticketPrice) {
        log.info("FlightService-generateTickets, flight ID: " + flightId);
        Flight flight = findFlightById(flightId);
        Airplane airplane = flight.getAirplane();

        int defaultFirstClassPercent = 30;
        double defaultTicketPrice = (airplane.getNumberOfSeats() * 4 + airplane.getAirplaneId()) / 2.0;
        double secondClassPriceCoefficient = 0.8;

        int seatsNumber = airplane.getNumberOfSeats();

        int percentOfFirstClassTickets = ticketPercent == 0 ? defaultFirstClassPercent : ticketPercent;
        double price = ticketPrice == 0 ? defaultTicketPrice : ticketPrice;

        int firstClassSeatsNumber = (int) Math.round(seatsNumber * percentOfFirstClassTickets / 100.0);
        int secondClassSeatsNumber = seatsNumber - firstClassSeatsNumber;

        double secondClassPrice = Math.ceil(price * secondClassPriceCoefficient);

        return new TicketsCreateRequest(flightId, firstClassSeatsNumber, secondClassSeatsNumber);
    }

    private boolean isFlightExist(Flight flightToCheck) {
        return flightRepository.findFlightByTrip_TripIdAndAirplane_AirplaneIdAndFlightNumberAndDepartureDateTimeAndArrivalDateTime(
                flightToCheck.getTrip().getTripId(), flightToCheck.getAirplane().getAirplaneId(), flightToCheck.getFlightNumber(),
                flightToCheck.getDepartureDateTime(), flightToCheck.getArrivalDateTime()).isPresent();
    }
}
