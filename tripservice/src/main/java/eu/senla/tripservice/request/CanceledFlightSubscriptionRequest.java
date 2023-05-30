package eu.senla.tripservice.request;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
public class CanceledFlightSubscriptionRequest {
    @NotNull
    private Long flightId;
}