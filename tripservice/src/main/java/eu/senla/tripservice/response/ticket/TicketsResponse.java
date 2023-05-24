package eu.senla.tripservice.response.ticket;

import eu.senla.tripservice.dto.TicketDTO;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class TicketsResponse {
    private List<TicketDTO> tickets;
}
