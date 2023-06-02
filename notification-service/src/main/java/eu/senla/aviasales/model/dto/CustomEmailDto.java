package eu.senla.aviasales.model.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * @author Mikhail.Leonovets
 * @since 05/2023
 */
@Getter
@Setter
public class CustomEmailDto {
    private String to;
    private String subject;
    private String body;
    private String email;

}
