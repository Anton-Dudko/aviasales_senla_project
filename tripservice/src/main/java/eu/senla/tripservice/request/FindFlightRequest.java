package eu.senla.tripservice.request;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString

public class FindFlightRequest {
    private String departureCity;
    private String arrivalCity;
    private String dateFrom;
    private int minSeatCount;
    private int maxSeatCount;
    private boolean firstClass;
    private boolean sortByDuration;
}
