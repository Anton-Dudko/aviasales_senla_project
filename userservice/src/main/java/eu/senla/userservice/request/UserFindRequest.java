package eu.senla.userservice.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserFindRequest {
    private String language;
    private String role;
    private LocalDate dateBirthFrom;
    private LocalDate dateBirthTo;
}
