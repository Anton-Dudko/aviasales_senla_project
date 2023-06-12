package eu.senla.userservice.request;

import eu.senla.userservice.exception.ExceptionMessageConstants;
import io.swagger.v3.oas.annotations.media.Schema;
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
public class UserUpdateRequest {

    @Schema(required = true)
    @NotEmpty
    @Email(regexp = ".+[@].+[\\.].+", message = ExceptionMessageConstants.NOT_VALID_EMAIL)
    private String email;

    @Pattern(regexp = "^[\\pL\\d]+[\\pL\\pP\\pZ\\d]{2,}$",
            message = ExceptionMessageConstants.ERROR_USERNAME_PATTERN)
    private String username;

    @Pattern(regexp = "^[\\pL\\d]+[\\pL\\pP\\d]{2,}$",
            message = ExceptionMessageConstants.ERROR_PASSWORD_PATTERN)
    private String password;

    @Schema(description = "yyyy-mm-dd")
    @Pattern(regexp = "(19|20)\\d\\d[-](0[1-9]|1[012])[-](0[1-9]|[12][0-9]|3[01])",
            message = ExceptionMessageConstants.NOT_VALID_DATE_FORMAT)
    private String dateBirth;

    @Schema(description = "EN, RU")
    private String language;
}
