package eu.senla.tripservice.entity;

import lombok.*;

import javax.persistence.*;

@Setter
@Getter
@NoArgsConstructor
@Builder
@AllArgsConstructor
@Entity(name = "subscriptions")
public class Subscription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "subscription_id")
    private long subscriptionId;

    @Column(name = "event_name")
    private String eventName;

    @Column(name = "user_id")
    private long userId;

    @Column(name = "trip_flight_id")
    private long tripFlightId;
}
