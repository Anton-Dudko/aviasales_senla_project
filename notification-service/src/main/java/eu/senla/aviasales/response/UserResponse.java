package eu.senla.aviasales.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
public class UserResponse {
    private Long userId;
    private String username;
    private String email;
    private LocalDate dateBirth;
    private String language;
    private String role;
}