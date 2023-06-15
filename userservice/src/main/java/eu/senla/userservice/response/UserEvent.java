package eu.senla.userservice.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class UserEvent {
    private String userName;
    private String email;
    private String newPassword;
    private String userLanguage;
}
