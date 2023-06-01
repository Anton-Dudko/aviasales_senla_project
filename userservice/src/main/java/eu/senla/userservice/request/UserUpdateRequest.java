package eu.senla.userservice.request;

import eu.senla.userservice.exception.ExceptionMessageConstants;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.Pattern;


@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserUpdateRequest {
    @NonNull
    @Email(regexp = ".+[@].+[\\.].+", message = ExceptionMessageConstants.NOT_VALID_EMAIL)
    private String email;
    private String username;
    private String password;
    @Pattern(regexp = "(19|20)\\d\\d[-](0[1-9]|1[012])[-](0[1-9]|[12][0-9]|3[01])",
            message = ExceptionMessageConstants.NOT_VALID_DATE_FORMAT)
    private String dateBirth;
    private String language;
    private String role;
}
