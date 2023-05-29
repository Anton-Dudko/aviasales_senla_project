package eu.senla.tripservice.response.ticket;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TicketResponse {
    private Long id;
    @JsonProperty("tripId")
    private Long flightId;
    private String type;
    private Double price;
    private String status;
}
