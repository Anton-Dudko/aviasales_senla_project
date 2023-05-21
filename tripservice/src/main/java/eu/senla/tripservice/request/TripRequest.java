package eu.senla.tripservice.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class TripRequest {
    @NotEmpty(message = "Departure city shouldn't be empty")
    @NotNull(message = "Departure city shouldn't be null")
    private String departureCity;

    @NotEmpty(message = "Arrival city shouldn't be empty")
    @NotNull(message = "Arrival city shouldn't be null")
    private String arrivalCity;
}
