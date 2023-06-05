package eu.senla.tripservice.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Setter
@Getter
@AllArgsConstructor
@Builder
public class FindFlightRequest {
    @NotNull
    @NotEmpty
    private String departureCity;
    @NotNull
    @NotEmpty
    private String arrivalCity;
    @NotNull
    @NotEmpty
    private String departureDate;
    private String returnDate;
    private int minSeatCount;
    private int maxSeatCount;
    private boolean firstClass;
    private boolean fastest;
    private boolean cheapest;
}
