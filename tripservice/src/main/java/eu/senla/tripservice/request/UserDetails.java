package eu.senla.tripservice.request;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class UserDetails {
    private long userId;
    private String language;
    private String email;
    private String username;
}