package eu.senla.aviasales.request;

import eu.senla.aviasales.exception.ExceptionMessageConstants;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

/**
 * @author Mikhail.Leonovets
 * @since 05/2023
 */

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CustomEmailRequest {

    private String userName;

    @NotEmpty
    @Email(regexp = ".+[@].+[\\.].+")
    private String email;

    @NotEmpty
    @Pattern(regexp = "^[\\pL\\d]+[\\pL\\pP\\pZ\\d]{4,100}$",
            message = ExceptionMessageConstants.SUBJECT_INCORRECT_LENGTH)
    private String subject;

    @NotEmpty
    @Pattern(regexp = "^[\\pL\\d]+[\\pL\\pP\\pZ\\d]{4,1000}$",
            message = ExceptionMessageConstants.BODY_INCORRECT_LENGTH)
    private String body;
}
