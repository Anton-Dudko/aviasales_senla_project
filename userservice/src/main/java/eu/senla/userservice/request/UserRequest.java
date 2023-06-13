package eu.senla.userservice.request;

import eu.senla.userservice.exception.ExceptionMessageConstants;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;


@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserRequest {

    @NotEmpty
    @Pattern(regexp = "^[\\pL\\d]+[\\pL\\pP\\pZ\\d]{2,}$",
            message = ExceptionMessageConstants.ERROR_USERNAME_PATTERN)
    private String username;

    @NotEmpty
    @Email(regexp = ".+[@].+[\\.].+",
            message = ExceptionMessageConstants.NOT_VALID_EMAIL)
    private String email;

    @NotEmpty
    @Pattern(regexp = "^[\\pL\\d]+[\\pL\\pP\\d]{2,}$",
            message = ExceptionMessageConstants.ERROR_PASSWORD_PATTERN)
    private String password;

    @NotEmpty
    @Pattern(regexp = "(19|20)\\d\\d[-](0[1-9]|1[012])[-](0[1-9]|[12][0-9]|3[01])",
            message = ExceptionMessageConstants.NOT_VALID_DATE_FORMAT)
    private String dateBirth;

    private String language;

    private String role;
}
