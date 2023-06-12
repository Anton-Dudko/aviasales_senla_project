package eu.senla.aviasales.request;

import eu.senla.aviasales.exception.ExceptionMessageConstants;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;

import javax.validation.constraints.Email;

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
    @NonNull
    private String userName;
    @NonNull
    @Email(regexp = ".+[@].+[\\.].+", message = ExceptionMessageConstants.NOT_VALID_EMAIL)
    private String email;
    @NonNull
    private String subject;
    @NonNull
    private String body;
}
