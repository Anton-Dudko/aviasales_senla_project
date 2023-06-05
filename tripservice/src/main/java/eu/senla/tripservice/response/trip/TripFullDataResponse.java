package eu.senla.tripservice.response.trip;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TripFullDataResponse {
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String message;
    private long tripId;
    private String departureCity;
    private String arrivalCity;
}
