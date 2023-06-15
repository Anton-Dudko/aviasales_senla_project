package eu.senla.userservice.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class NewPasswordRequest {

    @NotEmpty
    @Email(regexp = ".+[@].+[\\.].+")
    private String email;
}
