package com.aviasales.finance.dto.payment;

import com.aviasales.finance.validation.ValidCardDate;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.hibernate.validator.constraints.CreditCardNumber;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.time.YearMonth;
import java.util.List;

@Data
public class PaymentDto {
    @Size(min = 1, max = 10, message = "Number of tickets to pay should be more or equals 1 and less or equals 10")
    private List<Long> tickets;
    @CreditCardNumber(message = "Credit card number is not valid")
    private String cardNumber;
    @JsonFormat(pattern = "MM/yy")
    @ValidCardDate
    private YearMonth cardDate;

    @Size(min = 3, max = 4, message = "CVV must be between 3 and 4 digits")
    @Pattern(regexp = "^[0-9]*$", message = "CVV must contain only digits")
    private String cvv;
    private String cardHolder;
}