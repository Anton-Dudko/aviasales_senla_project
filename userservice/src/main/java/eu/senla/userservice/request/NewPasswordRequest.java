package eu.senla.userservice.request;

import eu.senla.userservice.exception.ExceptionMessageConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class NewPasswordRequest {

    @Schema(required = true)
    @NotEmpty
    @Email(regexp = ".+[@].+[\\.].+", message = ExceptionMessageConstants.NOT_VALID_EMAIL)
    private String email;
}
