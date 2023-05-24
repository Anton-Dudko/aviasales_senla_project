package eu.senla.tripservice.response.flight;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class ListFlightsFullDataResponse {
    private long total;
    private List<FlightFullDataResponse> tripResponseList;
}
