package eu.senla.tripservice.response;

import eu.senla.tripservice.entity.Trip;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class AllTripsResponse {
    private long count;
    private List<Trip> tripResponseList;
}
