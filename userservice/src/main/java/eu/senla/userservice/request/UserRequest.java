package eu.senla.userservice.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;

import javax.validation.constraints.Email;


@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UserRequest {
    @NonNull
    private String username;
    @NonNull
    @Email(regexp = ".+[@].+[\\.].+")
    private String email;
    @NonNull
    private String password;

    private String role;
}
