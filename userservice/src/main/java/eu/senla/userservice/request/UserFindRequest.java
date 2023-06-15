package eu.senla.userservice.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
@Builder
public class UserFindRequest {

    private String username;
    private String email;
    private String language;
    private String role;
    private LocalDate dateBirthFrom;
    private LocalDate dateBirthTo;
}
