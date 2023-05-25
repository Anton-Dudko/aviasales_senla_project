package com.aviasales.finance.dto;

import lombok.Data;
import org.hibernate.validator.constraints.CreditCardNumber;

import javax.validation.constraints.Pattern;

@Data
public class BlockCarDto {
    @CreditCardNumber(message = "Credit card number is not valid")
    private String cardNumber;
    @Pattern(regexp = "[A-Z]{2}", message = "Country code should be in ISO 3166-1 alpha-2 format")
    private String countryCode;

    public boolean validateDto() {
        return !((getCardNumber() == null && getCountryCode() == null)
                || (getCardNumber() != null && getCountryCode() != null));
    }
}
