package eu.senla.tripservice.request;

import lombok.Getter;
import lombok.Setter;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Getter
@Setter
public class NewFlightsSubscriptionRequest {
    @NotEmpty(message = "Departure city shouldn't be empty")
    @NotNull(message = "Departure city shouldn't be null")
    private String departureCity;

    @NotEmpty(message = "Arrival city shouldn't be empty")
    @NotNull(message = "Arrival city shouldn't be null")
    private String arrivalCity;
}