package eu.senla.aviasales.model.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * @author Mikhail.Leonovets
 * @since 05/2023
 */
@Getter
@Setter
public class PaymentErrorEventDto extends UserDataDto {
    private String paymentInfo;
    private String amountPayable;
    private String paymentDate;

}
