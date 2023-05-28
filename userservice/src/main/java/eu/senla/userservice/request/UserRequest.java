package eu.senla.userservice.request;

import eu.senla.userservice.exception.ExceptionMessageConstant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;

import javax.validation.constraints.Email;
import java.time.LocalDate;


@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserRequest {
    @NonNull
    private String username;
    @NonNull
    @Email(regexp = ".+[@].+[\\.].+", message = ExceptionMessageConstant.NOT_VALID_EMAIL)
    private String email;
    @NonNull
    private String password;

    private LocalDate dateBirth;
    private String language;
    private String role;
}
