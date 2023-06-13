package eu.senla.userservice.request;

import eu.senla.userservice.exception.ExceptionMessageConstants;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LoginRequest {

    @NotEmpty
    @Email(regexp = ".+[@].+[\\.].+", message = ExceptionMessageConstants.NOT_VALID_EMAIL)
    private String email;

    @NotEmpty
    private String password;
}
