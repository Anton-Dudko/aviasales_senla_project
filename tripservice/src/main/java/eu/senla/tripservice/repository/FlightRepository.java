package eu.senla.tripservice.repository;

import eu.senla.tripservice.entity.Flight;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FlightRepository extends JpaRepository<Flight, Long> {
}
