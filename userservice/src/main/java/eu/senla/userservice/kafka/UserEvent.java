package eu.senla.userservice.kafka;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserEvent {
    private String userName;
    private String email;
    private String password;
    private String userLanguage;
}
