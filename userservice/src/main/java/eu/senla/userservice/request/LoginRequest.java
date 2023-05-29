package eu.senla.userservice.request;

import eu.senla.userservice.exception.ExceptionMessageConstant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;

import javax.validation.constraints.Email;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LoginRequest {
    @NonNull
    @Email(regexp = ".+[@].+[\\.].+", message = ExceptionMessageConstant.NOT_VALID_EMAIL)
    private String email;
    @NonNull
    private String password;
}
