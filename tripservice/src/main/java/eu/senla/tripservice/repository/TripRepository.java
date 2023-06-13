package eu.senla.tripservice.repository;

import eu.senla.tripservice.entity.Trip;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TripRepository extends JpaRepository<Trip, Long> {
    Optional<Trip> findByDepartureCityAndArrivalCity(String departureCity,
                                                     String arrivalCity);
}
