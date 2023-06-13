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
public class FlightRequest {
    @NotNull
    private Long tripId;

    @NotNull
    private Long airplaneId;

    @NotEmpty
    private String flightNumber;

    @NotEmpty
    private String departureDateTime;

    @NotEmpty
    private String arrivalDateTime;

    private boolean canceled;

    private boolean meal;

    private boolean handLuggage;

    private boolean luggage;

    private int firstClassTicketPercent;

    private double ticketPrice;
}
