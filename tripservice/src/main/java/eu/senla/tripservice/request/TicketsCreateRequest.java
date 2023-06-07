package eu.senla.tripservice.request;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class TicketsCreateRequest {
    long flightId;
    int countTicketsFirstClass;
    int countTicketsSecondClass;
    double firstClassPrice;
    double secondClassPrice;
}
