package eu.senla.tripservice.repository;

import eu.senla.tripservice.entity.Flight;
import eu.senla.tripservice.entity.Trip;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface FlightRepository extends JpaRepository<Flight, Long> {
    Optional<Flight> findFlightByTrip_TripIdAndAirplane_AirplaneIdAndFlightNumberAndDepartureDateTimeAndArrivalDateTime(
            long tripId, long airplaneId, String flightNumber, LocalDateTime departureDateTime, LocalDateTime arrivalDateTime);

    List<Flight> findAllByTripAndDepartureDateTimeBetween(Trip trip, LocalDateTime dateTimeFrom, LocalDateTime dateTimeTo);

}
