package eu.senla.tripservice.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
@ToString
@Entity(name = "trips")
public class Trip {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "trip_id")
    private long tripId;

    @Column(name = "departure_city")
    @NotEmpty(message = "Departure city shouldn't be empty")
    @NotNull(message = "Departure city shouldn't be null")
    private String departureCity;

    @Column(name = "arrival_city")
    @NotEmpty(message = "Arrival city shouldn't be empty")
    @NotNull(message = "Arrival city shouldn't be null")
    private String arrivalCity;
}
