package eu.senla.userservice.request;

import eu.senla.userservice.exception.ExceptionMessageConstants;
import lombok.AllArgsConstructor;
import lombok.Getter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Past;
import javax.validation.constraints.Pattern;
import java.time.LocalDate;


@Getter
@AllArgsConstructor
public class UserUpdateRequest {

    @NotEmpty
    @Email(regexp = ".+[@].+[\\.].+")
    private String email;

    @Pattern(regexp = "^[\\pL\\d]+[\\pL\\pP\\pZ\\d]{2,}$",
            message = ExceptionMessageConstants.ERROR_USERNAME_PATTERN)
    private String username;

    @Pattern(regexp = "^[\\pL\\d]+[\\pL\\pP\\d]{2,}$",
            message = ExceptionMessageConstants.ERROR_PASSWORD_PATTERN)
    private String password;

    @Past
    private LocalDate dateBirth;

    private String language;
}
