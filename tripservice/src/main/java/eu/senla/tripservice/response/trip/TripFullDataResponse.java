package eu.senla.tripservice.response.trip;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TripFullDataResponse {
    private long tripId;
    private String departureCity;
    private String arrivalCity;
}
