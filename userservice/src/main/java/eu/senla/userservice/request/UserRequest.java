package eu.senla.userservice.request;

import eu.senla.userservice.exception.ExceptionMessageConstants;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import javax.validation.constraints.Pattern;
import java.time.LocalDate;


@Getter
@Setter
@AllArgsConstructor
@Builder
public class UserRequest {

    @NotEmpty
    @Pattern(regexp = "^[\\pL\\d]+[\\pL\\pP\\pZ\\d]{2,}$",
            message = ExceptionMessageConstants.ERROR_USERNAME_PATTERN)
    private String username;

    @NotEmpty
    @Email(regexp = ".+[@].+[\\.].+")
    private String email;

    @NotEmpty
    @Pattern(regexp = "^[\\pL\\d]+[\\pL\\pP\\d]{2,}$",
            message = ExceptionMessageConstants.ERROR_PASSWORD_PATTERN)
    private String password;

    @NotNull
    @Past
    private LocalDate dateBirth;

    private String language;

    private String role;
}
