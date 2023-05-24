package eu.senla.tripservice.response.flight;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@ToString
public class FlightResponse {
    private long flightId;
    private String departureCity;
    private String arrivalCity;
    private String flightNumber;
    private String departureDateTime;
    private String arrivalDateTime;
}
