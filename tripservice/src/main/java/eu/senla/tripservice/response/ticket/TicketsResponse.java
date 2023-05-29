package eu.senla.tripservice.response.ticket;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class TicketsResponse {
    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    private int count;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String message;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonProperty("list")
    private List<TicketResponse> tickets;
}
