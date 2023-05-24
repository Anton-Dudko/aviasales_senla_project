package eu.senla.tripservice.response.flight;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ListFlightsResponse {
    private int total;
    List<FlightResponse> flights;
}
