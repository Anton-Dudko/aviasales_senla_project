package com.aviasales.finance.dto.payment;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.hibernate.validator.constraints.CreditCardNumber;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.YearMonth;

@Data
public class TransactionDto {
    @CreditCardNumber(message = "Credit card number is not valid")
    private String cardNumber;
    @JsonFormat(pattern = "MM/yy")
    private YearMonth cardDate;

    @Size(min = 3, max = 4, message = "CVV must be between 3 and 4 digits")
    @Pattern(regexp = "^[0-9]*$", message = "CVV must contain only digits")
    private String cvv;
    private String cardHolder;

    @DecimalMin(value = "0.01", message = "Sum is less than minimal")
    private BigDecimal sum;
}
