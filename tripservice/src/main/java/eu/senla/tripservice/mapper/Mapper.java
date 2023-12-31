package eu.senla.tripservice.mapper;

import eu.senla.tripservice.entity.Airplane;
import eu.senla.tripservice.entity.Flight;
import eu.senla.tripservice.entity.Trip;
import eu.senla.tripservice.request.FlightRequest;
import eu.senla.tripservice.request.TripRequest;
import eu.senla.tripservice.response.flight.FlightFullDataResponse;
import eu.senla.tripservice.response.flight.FlightInfo;
import eu.senla.tripservice.response.flight.FlightResponse;
import eu.senla.tripservice.response.trip.TripFullDataResponse;
import eu.senla.tripservice.response.trip.TripResponse;
import eu.senla.tripservice.util.time.TimeFormatter;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;
import java.time.Duration;
import java.time.LocalDateTime;

@Component
public class Mapper {
    public FlightResponse mapFlightToFlightResponse(@NotNull Flight flight) {
        return FlightResponse.builder()
                .flightId(flight.getFlightId())
                .departureCity(flight.getTrip().getDepartureCity())
                .arrivalCity(flight.getTrip().getArrivalCity())
                .flightNumber(flight.getFlightNumber())
                .departureDateTime(TimeFormatter.formatLocalDateTimeToString(flight.getDepartureDateTime()))
                .arrivalDateTime(TimeFormatter.formatLocalDateTimeToString(flight.getArrivalDateTime()))
                .build();
    }

    public FlightInfo mapFlightToFlightInfo(@NotNull Flight flight) {
        return FlightInfo.builder()
                .flightId(flight.getFlightId())
                .departureCity(flight.getTrip().getDepartureCity())
                .arrivalCity(flight.getTrip().getArrivalCity())
                .flightNumber(flight.getFlightNumber())
                .departureDateTime(TimeFormatter.formatLocalDateTimeToString(flight.getDepartureDateTime()))
                .arrivalDateTime(TimeFormatter.formatLocalDateTimeToString(flight.getArrivalDateTime()))
                .airplaneModel(flight.getAirplane().getModel())
                .meal(flight.isMeal())
                .handLuggage(flight.isHandLuggage())
                .luggage(flight.isLuggage())
                .socket(flight.getAirplane().isSocket())
                .media(flight.getAirplane().isMedia())
                .wiFi(flight.getAirplane().isWiFi())
                .seatPitch(flight.getAirplane().getSeatPitch())
                .seatWidth(flight.getAirplane().getSeatWidth())
                .chairScheme(flight.getAirplane().getChairScheme())
                .build();
    }

    public Flight mapFlightRequestToFlight(@NotNull FlightRequest flightRequest,
                                           @NotNull Trip trip,
                                           @NotNull Airplane airplane) {
        LocalDateTime departureDateTime = TimeFormatter.formatStringToDateTime(flightRequest.getDepartureDateTime());
        LocalDateTime arrivalDateTime = TimeFormatter.formatStringToDateTime(flightRequest.getArrivalDateTime());

        return Flight.builder()
                .trip(trip)
                .airplane(airplane)
                .flightNumber(flightRequest.getFlightNumber())
                .departureDateTime(departureDateTime)
                .arrivalDateTime(arrivalDateTime)
                .meal(flightRequest.isMeal())
                .handLuggage(flightRequest.isHandLuggage())
                .luggage(flightRequest.isLuggage())
                .duration(Duration.between(departureDateTime, arrivalDateTime).toMinutes())
                .canceled(flightRequest.isCanceled())
                .build();
    }

    public FlightFullDataResponse mapFlightToFlightFullDataResponse(@NotNull Flight flight) {
        return FlightFullDataResponse.builder()
                .flightId(flight.getFlightId())
                .trip(flight.getTrip())
                .airplane(flight.getAirplane())
                .flightNumber(flight.getFlightNumber())
                .departureDateTime(flight.getDepartureDateTime())
                .arrivalDateTime(flight.getArrivalDateTime())
                .duration(flight.getDuration())
                .meal(flight.isMeal())
                .handLuggage(flight.isHandLuggage())
                .luggage(flight.isLuggage())
                .build();
    }

    public Trip mapTripRequestToTrip(@NotNull TripRequest tripRequest) {
        return Trip.builder()
                .departureCity(tripRequest.getDepartureCity())
                .arrivalCity(tripRequest.getArrivalCity())
                .build();
    }

    public TripFullDataResponse mapTripToTripFullDataResponse(@NotNull Trip trip) {
        return TripFullDataResponse.builder()
                .tripId(trip.getTripId())
                .departureCity(trip.getDepartureCity())
                .arrivalCity(trip.getArrivalCity())
                .build();
    }

    public TripResponse mapTripToTripResponse(@NotNull Trip trip) {
        return TripResponse.builder()
                .departureCity(trip.getDepartureCity())
                .arrivalCity(trip.getArrivalCity())
                .build();
    }
}
