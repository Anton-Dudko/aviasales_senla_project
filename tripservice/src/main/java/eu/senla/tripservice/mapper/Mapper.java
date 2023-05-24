package eu.senla.tripservice.mapper;

import eu.senla.tripservice.entity.Airplane;
import eu.senla.tripservice.entity.Flight;
import eu.senla.tripservice.entity.Subscription;
import eu.senla.tripservice.entity.Trip;
import eu.senla.tripservice.request.FlightRequest;
import eu.senla.tripservice.request.SubscriptionRequest;
import eu.senla.tripservice.request.TripRequest;
import eu.senla.tripservice.response.flight.FlightFullDataResponse;
import eu.senla.tripservice.response.flight.FlightInfo;
import eu.senla.tripservice.response.flight.FlightResponse;
import eu.senla.tripservice.response.trip.TripFullDataResponse;
import eu.senla.tripservice.response.trip.TripResponse;
import eu.senla.tripservice.util.time.TimeFormatter;
import org.springframework.stereotype.Component;

@Component
public class Mapper {
    public FlightResponse mapFlightToFlightResponse(Flight flight) {
        return FlightResponse.builder()
                .flightId(flight.getFlightId())
                .departureCity(flight.getTrip().getDepartureCity())
                .arrivalCity(flight.getTrip().getArrivalCity())
                .flightNumber(flight.getFlightNumber())
                .departureDateTime(TimeFormatter.formatLocalDateTimeToString(flight.getDepartureDateTime()))
                .arrivalDateTime(TimeFormatter.formatLocalDateTimeToString(flight.getArrivalDateTime()))
                .build();
    }

    public FlightInfo mapFlightToFlightInfo(Flight flight) {
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

    public Flight mapFlightRequestToFlight(FlightRequest flightRequest, Trip trip, Airplane airplane) {
        return Flight.builder()
                .trip(trip)
                .airplane(airplane)
                .flightNumber(flightRequest.getFlightNumber())
                .departureDateTime(TimeFormatter.formatStringToDateTime(flightRequest.getDepartureDateTime()))
                .arrivalDateTime(TimeFormatter.formatStringToDateTime(flightRequest.getArrivalDateTime()))
                .meal(flightRequest.isMeal())
                .handLuggage(flightRequest.isHandLuggage())
                .luggage(flightRequest.isLuggage())
                .build();
    }

    public FlightFullDataResponse mapFlightToFlightFullDataResponse(Flight flight) {
        return FlightFullDataResponse.builder()
                .flightId(flight.getFlightId())
                .trip(flight.getTrip())
                .airplane(flight.getAirplane())
                .flightNumber(flight.getFlightNumber())
                .departureDateTime(flight.getDepartureDateTime())
                .arrivalDateTime(flight.getArrivalDateTime())
                .meal(flight.isMeal())
                .handLuggage(flight.isHandLuggage())
                .luggage(flight.isLuggage())
                .build();
    }

    public Trip mapTripRequestToTrip(TripRequest tripRequest) {
        return Trip.builder()
                .departureCity(tripRequest.getDepartureCity())
                .arrivalCity(tripRequest.getArrivalCity())
                .build();
    }

    public TripFullDataResponse mapTripToTripFullDataResponse(Trip trip) {
        return TripFullDataResponse.builder()
                .tripId(trip.getTripId())
                .departureCity(trip.getDepartureCity())
                .arrivalCity(trip.getArrivalCity())
                .build();

    }

    public TripResponse mapTripToTripResponse(Trip trip) {
        return TripResponse.builder()
                .departureCity(trip.getDepartureCity())
                .arrivalCity(trip.getArrivalCity())
                .build();

    }

    public Subscription mapSubscriptionRequestToSubscription(SubscriptionRequest subscriptionRequest) {
        return Subscription.builder()
                .eventName(subscriptionRequest.getEventName())
                .userId(subscriptionRequest.getUserId())
                .tripFlightId(subscriptionRequest.getTripFlightId())
                .build();
    }
}
