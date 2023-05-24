package eu.senla.tripservice.response.flight;

import eu.senla.tripservice.dto.TicketDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@Builder
@AllArgsConstructor
public class FlightInfo {
    private long flightId;
    private List<TicketDTO> tickets;
    private String departureCity;
    private String arrivalCity;
    private String flightNumber;
    private String departureDateTime;
    private String arrivalDateTime;
    private String airplaneModel;
    private boolean meal;
    private boolean handLuggage;
    private boolean luggage;
    private boolean socket;
    private boolean media;
    private boolean wiFi;
    private int seatPitch;
    private int seatWidth;
    private String chairScheme;
}
