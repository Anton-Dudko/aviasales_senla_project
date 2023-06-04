package eu.senla.aviasales.model.dto;

import eu.senla.aviasales.exception.ExceptionMessageConstants;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import javax.validation.constraints.Email;

/**
 * @author Mikhail.Leonovets
 * @since 05/2023
 */
@Getter
@Setter
public class CustomEmailDto {
    @NonNull
    String userName;
    @NonNull
    @Email(regexp = ".+[@].+[\\.].+", message = ExceptionMessageConstants.NOT_VALID_EMAIL)
    private String email;
    @NonNull
    private String subject;
    @NonNull
    private String body;
}
