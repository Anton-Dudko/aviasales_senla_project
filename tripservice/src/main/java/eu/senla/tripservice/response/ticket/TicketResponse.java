package eu.senla.tripservice.response.ticket;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TicketResponse {
    private Long id;
    private String type;
    private Double price;
    private String status;
}
