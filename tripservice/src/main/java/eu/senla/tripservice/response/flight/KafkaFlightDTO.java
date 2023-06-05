package eu.senla.tripservice.response.flight;

import eu.senla.tripservice.util.time.TimeFormatter;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class KafkaFlightDTO {
    private String userLanguage;
    private String email;
    private String userName;
    private String from;
    private String to;
    private String tripDate;
    private Double price;

    public void setTripDate(LocalDateTime tripDate) {
        this.tripDate = TimeFormatter.formatLocalDateTimeToString(tripDate);
    }
}
