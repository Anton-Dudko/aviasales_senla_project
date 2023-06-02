package eu.senla.aviasales.model.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserUpdatePasswordEventDto extends UserDataDto {
    private String newPassword;

}
