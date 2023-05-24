package eu.senla.tripservice.request;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@Setter
public class SubscriptionRequest {
    @NotNull
    @NotEmpty(message = "Event name mustn't be empty")
    private String eventName;

    @Size(min = 1, message = "User id must be more than 0")
    private long userId;

    @Size(min = 1, message = "Trip or Flight id must be more than 0")
    private long tripFlightId;
}
