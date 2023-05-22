package eu.senla.aviasales.model.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * @author Mikhail.Leonovets
 * @since 05/2023
 */
@Getter
@Setter
public class ResetPasswordDto {
    private String email;
    private String code;

}
