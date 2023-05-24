package eu.senla.tripservice.response.flight;

import eu.senla.tripservice.entity.Airplane;
import eu.senla.tripservice.entity.Trip;
import lombok.*;

import java.time.LocalDateTime;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FlightFullDataResponse {
    private long flightId;
    private Trip trip;
    private Airplane airplane;
    private String flightNumber;
    private LocalDateTime departureDateTime;
    private LocalDateTime arrivalDateTime;
    private boolean meal;
    private boolean handLuggage;
    private boolean luggage;
}
