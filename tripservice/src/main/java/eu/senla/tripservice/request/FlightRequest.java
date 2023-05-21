package eu.senla.tripservice.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class FlightRequest {

    @NotNull
    private long tripId;

    @NotNull
    private long airplaneId;

    @NotNull
    private String flightNumber;

    @NotNull
    private String departureDateTime;

    @NotNull
    private String arrivalDateTime;

    private boolean meal;

    private boolean HandLuggage;

    private boolean luggage;
}
