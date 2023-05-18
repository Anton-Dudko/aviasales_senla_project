package com.aviasales.finance.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.hibernate.validator.constraints.CreditCardNumber;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.time.YearMonth;

@Data
public class PaymentDto {
    @NotBlank(message = "Ticked it field is mandatory")
    private String ticketId;
    @CreditCardNumber(message = "Credit card number is not valid")
    private String cardNumber;
    @JsonFormat(pattern = "MM/yy")
    private YearMonth cardDate;

    @Size(min = 3, max = 4, message = "CVV must be between 3 and 4 digits")
    @Pattern(regexp = "^[0-9]*$", message = "CVV must contain only digits")
    private String cvv;
    private String cardHolderName;
}