package eu.senla.aviasales.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * @author Mikhail.Leonovets
 * @since 05/2023
 */
@Getter
@Setter
public class CustomEmailDto {
    private String topic;
    private String body;
    private String email;

}
