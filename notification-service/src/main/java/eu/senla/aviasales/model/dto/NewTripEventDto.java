package eu.senla.aviasales.model.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * @author Mikhail.Leonovets
 * @since 05/2023
 */
@Getter
@Setter
public class NewTripEventDto extends UserDataDto {
    private String from;
    private String to;
    private String tripDate;
    private String price;

}
