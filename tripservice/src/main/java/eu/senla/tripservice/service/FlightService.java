package eu.senla.tripservice.service;

import eu.senla.tripservice.entity.Airplane;
import eu.senla.tripservice.entity.Flight;
import eu.senla.tripservice.entity.Trip;
import eu.senla.tripservice.exeption.flight.FlightAlreadyExistsException;
import eu.senla.tripservice.exeption.flight.FlightNotFoundException;
import eu.senla.tripservice.exeption.ticket.RequestException;
import eu.senla.tripservice.mapper.Mapper;
import eu.senla.tripservice.repository.FlightRepository;
import eu.senla.tripservice.request.FindFlightRequest;
import eu.senla.tripservice.request.FlightRequest;
import eu.senla.tripservice.request.TicketsCreateRequest;
import eu.senla.tripservice.response.flight.*;
import eu.senla.tripservice.response.ticket.TicketResponse;
import eu.senla.tripservice.response.ticket.TicketsResponse;
import eu.senla.tripservice.util.KafkaTopicsName;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional(readOnly = true)
public class FlightService {
    private final FlightRepository flightRepository;
    private final TripService tripService;
    private final AirplaneService airplaneService;
    private final Mapper mapper;
    private final KafkaService kafkaService;

    @Autowired
    public FlightService(FlightRepository flightRepository,
                         TripService tripService,
                         AirplaneService airplaneService,
                         Mapper mapper,
                         KafkaService kafkaService) {
        this.flightRepository = flightRepository;
        this.tripService = tripService;
        this.airplaneService = airplaneService;
        this.mapper = mapper;
        this.kafkaService = kafkaService;
    }

    @Transactional
    public Flight create(@NotNull FlightRequest flightRequest) {
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

        kafkaService.newEvent(KafkaTopicsName.NEW_FLIGHT_EVENT,
                flightToSave.getFlightId(),
                flightToSave.getTrip().getTripId(),
                flightToSave.getDepartureDateTime());

        return flightToSave;
    }

    public FlightFullDataResponse findById(@NotNull Long id) {
        return mapper.mapFlightToFlightFullDataResponse(findFlightById(id));
    }

    private Flight findFlightById(@NotNull Long id) {
        log.info("FlightService-findById: " + id);
        return flightRepository.findById(id)
                .orElseThrow(() -> new FlightNotFoundException("Flight with id = " + id + " not found"));
    }

    public ListFlightsFullDataResponse findAll(@NotNull PageRequest pageRequest) {
        log.info("FlightService-findAll");
        ListFlightsFullDataResponse listFlightsFullDataResponse = new ListFlightsFullDataResponse();
        listFlightsFullDataResponse.setTripResponseList(flightRepository.findAll(pageRequest)
                .stream()
                .map(mapper::mapFlightToFlightFullDataResponse)
                .collect(Collectors.toList()));
        listFlightsFullDataResponse.setTotal(listFlightsFullDataResponse.getTripResponseList().size());
        return listFlightsFullDataResponse;
    }

    public ListFlightsResponse find(@NotNull FindFlightRequest findFlightRequest,
                                    @NotNull Pageable pageable) {
        log.info("FlightService-find");
        ListFlightsResponse listFlightsResponse = new ListFlightsResponse();
        findDirectFlight(listFlightsResponse, findFlightRequest, pageable);

        if (findFlightRequest.getReturnDate() != null && !findFlightRequest.getReturnDate().isEmpty()) {
            findReturnFlight(listFlightsResponse, findFlightRequest);
        }
        return listFlightsResponse;
    }

    private void findDirectFlight(@NotNull ListFlightsResponse listFlightsResponse,
                                  @NotNull FindFlightRequest findFlightRequest,
                                  @NotNull Pageable pageable) {
        log.info("FlightService-findDirectFlight");
        if (findFlightRequest.isFastest() && findFlightRequest.isCheapest()) {
            log.info("FlightService-find: selected fastest and cheapest");
            listFlightsResponse.setMessage("Only one option must be selected: fastest or cheapest");

            return;

        } else if (findFlightRequest.isFastest()) {
            log.info("FlightService-findDirectFlight: fastest");
            List<Flight> sortedFlights = flightRepository.findAll(new FlightSpecification(findFlightRequest),
                    Sort.by("duration"));
            if (!sortedFlights.isEmpty()) {
                listFlightsResponse.setFlights(
                        new ArrayList<>(Collections.singleton(mapper.mapFlightToFlightResponse(sortedFlights.get(0)))));
                return;
            }

        } else if (findFlightRequest.isCheapest()) {
            log.info("FlightService-findDirectFlight: cheapest");
            List<Flight> flights = flightRepository.findAll(new FlightSpecification(findFlightRequest));
            if (!flights.isEmpty()) {
                List<TicketsResponse> ticketsResponses = new ArrayList<>();

                for (Flight flight : flights) {
                    ticketsResponses.add(makeGetTicketsRequest(flight.getFlightId()));
                }

                ticketsResponses.sort((o1, o2) -> {
                    sortTickets(o1.getTickets());
                    sortTickets(o2.getTickets());

                    return (int) Math.round(o1.getTickets().get(0).getPrice() - o2.getTickets().get(0).getPrice());
                });

                long cheapestFlightId = ticketsResponses.get(0).getTickets().get(0).getFlightId();
                log.info("FlightService-findDirectFlight: cheapestFlightId = " + cheapestFlightId);
                Flight cheapestFlight = null;

                for (Flight flight : flights) {
                    if (flight.getFlightId() == cheapestFlightId) {
                        cheapestFlight = flight;
                        break;
                    }
                }

                if (cheapestFlight != null) {
                    listFlightsResponse.setFlights(Collections.singletonList(mapper.mapFlightToFlightResponse(cheapestFlight)));
                    return;
                }
            }
        } else {
            log.info("FlightService-findDirectFlight: not fastest or cheapest");
            Page<Flight> flights;

            flights = flightRepository.findAll(new FlightSpecification(findFlightRequest), pageable);

            if (flights.hasContent()) {
                listFlightsResponse.setFlights(flights.getContent().stream()
                        .map(mapper::mapFlightToFlightResponse)
                        .collect(Collectors.toList()));
                listFlightsResponse.setTotal(flights.getContent().size());
                return;
            }
        }
        log.info("FlightService-findDirectFlight: not found");
        listFlightsResponse.setMessage("Flights: " + findFlightRequest.getDepartureCity() +
                " - " + findFlightRequest.getArrivalCity() + " not found: ");

    }

    private void findReturnFlight(@NotNull ListFlightsResponse listFlightsResponse,
                                  @NotNull FindFlightRequest findFlightRequest) {
        log.info("FlightService-findReturnFlight");
        FindFlightRequest returnFlight = FindFlightRequest.builder()
                .departureCity(findFlightRequest.getArrivalCity())
                .arrivalCity(findFlightRequest.getDepartureCity())
                .departureDate(findFlightRequest.getReturnDate())
                .build();

        List<FlightResponse> returnFlights = flightRepository.findAll(new FlightSpecification(returnFlight)).stream()
                .map(mapper::mapFlightToFlightResponse).toList();

        if (returnFlights.isEmpty()) {
            log.info("FlightService-findReturnFlight: not found");
            String message = "No return flights on date: " + findFlightRequest.getReturnDate();
            if (listFlightsResponse.getMessage() == null) {
                listFlightsResponse.setMessage(message);
            } else {
                listFlightsResponse.setMessage(listFlightsResponse.getMessage() + message);
            }
        } else {
            listFlightsResponse.setReturnFlights(returnFlights);
            listFlightsResponse.setReturnTotal(returnFlights.size());
        }

    }

    @Transactional
    public Flight update(@NotNull Long id,
                         @NotNull FlightRequest flightRequest) {
        log.info("FlightService-update");
        Flight flightToUpdate = findFlightById(id);
        Trip trip = tripService.findTripById(flightRequest.getTripId());
        Airplane airplane = airplaneService.findById(flightRequest.getAirplaneId());
        Flight updatedFlight = mapper.mapFlightRequestToFlight(flightRequest, trip, airplane);
        updatedFlight.setFlightId(id);
        if (isFlightExist(updatedFlight)) {
            throw new FlightAlreadyExistsException("The same flight already exists");
        }
        if (flightRequest.isCanceled() && !flightToUpdate.isCanceled()) {
            kafkaService.newEvent(KafkaTopicsName.FLIGHT_CANCELED_EVENT,
                    id,
                    trip.getTripId(),
                    updatedFlight.getDepartureDateTime());
        }
        flightRepository.save(updatedFlight);
        log.info("Flight with id: " + id + " was updated");
        return updatedFlight;
    }

    @Transactional
    public FlightFullDataResponse delete(@NotNull Long id) {
        log.info("FlightService-delete, id: " + id);
        Flight flightToDelete = findFlightById(id);
        flightRepository.deleteById(id);
        log.info("Flight id: " + id + " was deleted");
        FlightFullDataResponse response = mapper.mapFlightToFlightFullDataResponse(flightToDelete);
        response.setMessage("Flight was deleted");
        return response;
    }

    public FlightInfo info(@NotNull Long id) {
        log.info("FlightService-info, id: " + id);
        FlightInfo flightInfo = mapper.mapFlightToFlightInfo(findFlightById(id));
        flightInfo.setTickets(makeGetTicketsRequest(id));
        return flightInfo;
    }

    public List<Flight> findAllByIds(@NotNull List<Long> ids) {
        log.info("FlightService-findByIds");
        return flightRepository.findAllById(ids);
    }

    private void makeCreateTicketsRequest(@NotNull TicketsCreateRequest ticketsCreateRequest) {
        log.info("FlightService-makeCreateTicketsRequest");
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<TicketsCreateRequest> request = new HttpEntity<>(ticketsCreateRequest, headers);
        String createTicketsRequestUrl = "http://ticket-service:8088/tickets/generate";
        String receivedMessage;
        try {
            log.info("FlightService-makeCreateTicketsRequest - send generate tickets request: " + request);
            receivedMessage = restTemplate.postForObject(createTicketsRequestUrl, request, String.class);
        } catch (Exception e) {
            log.error("FlightService-makeCreateTicketsRequest - response from ticket-service:"
                    + e.getLocalizedMessage());
            throw new RequestException("Ticket not created");
        }
        log.info("FlightService-makeCreateTicketsRequest-receivedMessage: " + receivedMessage);
    }

    protected TicketsResponse makeGetTicketsRequest(@NotNull Long id) {
        log.info("Flight-service-makeGetTicketsRequest");
        RestTemplate restTemplate = new RestTemplate();

        String getTicketsRequestUrl = "http://ticket-service:8088/tickets?tripId=" + id;

        TicketsResponse response;

        try {
            response = restTemplate.getForObject(getTicketsRequestUrl, TicketsResponse.class);
        } catch (HttpClientErrorException e) {
            throw new RequestException(e.getResponseBodyAsString());
        }

        if (response == null || response.getTickets().isEmpty()) {
            response = new TicketsResponse();
            response.setMessage("Tickets not received");
        }
        return response;
    }

    private TicketsCreateRequest generateTickets(@NotNull Long flightId,
                                                 @NotNull Integer ticketPercent,
                                                 @NotNull Double ticketPrice) {
        log.info("FlightService-generateTickets, flight ID: " + flightId);
        Flight flight = findFlightById(flightId);
        Airplane airplane = flight.getAirplane();

        double firstClassTicketSurcharge = 1.2;
        int defaultFirstClassPercent = 30;
        double defaultTicketPrice = Math.ceil((airplane.getNumberOfSeats() * 4 + airplane.getAirplaneId()) / 2.0);

        int seatsNumber = airplane.getNumberOfSeats();

        int percentOfFirstClassTickets = ticketPercent == 0 ? defaultFirstClassPercent : ticketPercent;
        double price = ticketPrice == 0 ? defaultTicketPrice : ticketPrice;

        int firstClassSeatsNumber = (int) Math.round(seatsNumber * percentOfFirstClassTickets / 100.0);
        int secondClassSeatsNumber = seatsNumber - firstClassSeatsNumber;

        return new TicketsCreateRequest(flightId, firstClassSeatsNumber, secondClassSeatsNumber,
                price * firstClassTicketSurcharge, price);
    }

    private boolean isFlightExist(@NotNull Flight flightToCheck) {
        return flightRepository.findFlightByTrip_TripIdAndAirplane_AirplaneIdAndFlightNumberAndDepartureDateTimeAndArrivalDateTime(
                flightToCheck.getTrip().getTripId(),
                flightToCheck.getAirplane().getAirplaneId(),
                flightToCheck.getFlightNumber(),
                flightToCheck.getDepartureDateTime(),
                flightToCheck.getArrivalDateTime()).isPresent();
    }

    protected void sortTickets(@NotNull List<TicketResponse> tickets) {
        tickets.sort((o1, o2) -> (int) Math.round(o1.getPrice() - o2.getPrice()));
    }
}
