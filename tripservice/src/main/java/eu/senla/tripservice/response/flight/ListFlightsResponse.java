package eu.senla.tripservice.response.flight;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ListFlightsResponse {
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String message;
    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    private int total;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<FlightResponse> flights;
    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    private int returnTotal;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<FlightResponse> returnFlights;
}
