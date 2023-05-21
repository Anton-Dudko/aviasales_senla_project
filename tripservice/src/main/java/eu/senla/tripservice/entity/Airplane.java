package eu.senla.tripservice.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;

@Entity(name = "airplanes")
@Getter
@Setter
@NoArgsConstructor
@ToString
public class Airplane {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "airplane_id")
    private long airplaneId;

    @Column(name = "model")
    private String model;

    @Column(name = "number_of_seats")
    private int numberOfSeats;

    @Column(name = "socket")
    private boolean socket;

    @Column(name = "media")
    private boolean media;

    @Column(name = "wi_fi")
    private boolean wiFi;

    @Column(name = "seat_pitch")
    private int seatPitch;

    @Column(name = "seat_width")
    private int seatWidth;

    @Column(name = "chair_scheme")
    private String chairScheme;
}
