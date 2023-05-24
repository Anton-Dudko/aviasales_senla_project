package eu.senla.tripservice.request;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class FindFlightRequest {
    private String departureCity;
    private String arrivalCity;
    private String dateFrom;
    private String dateTo;
}
