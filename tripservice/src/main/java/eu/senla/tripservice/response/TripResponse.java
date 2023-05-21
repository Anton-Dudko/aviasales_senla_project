package eu.senla.tripservice.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class TripResponse {
    private long id;
    private String departureCity;
    private String arrivalCity;
}
