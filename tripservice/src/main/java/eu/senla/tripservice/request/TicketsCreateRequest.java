package eu.senla.tripservice.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TicketsCreateRequest {
    private long flightId;
    private long firstClassTicketsNumber;
    private long secondClassTicketsNumber;
}
