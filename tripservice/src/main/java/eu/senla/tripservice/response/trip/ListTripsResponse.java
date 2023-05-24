package eu.senla.tripservice.response.trip;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class ListTripsResponse {
    private long total;
    private List<TripResponse> tripFullDataResponseList;
}
