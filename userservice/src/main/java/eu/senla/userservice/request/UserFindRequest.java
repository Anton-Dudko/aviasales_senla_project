package eu.senla.userservice.request;

import io.swagger.v3.oas.annotations.media.Schema;
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

    private String username;
    private String email;

    @Schema(description = "RU, EN")
    private String language;

    @Schema(description = "ROLE_ADMIN, ROLE_USER")
    private String role;

    @Schema(description = "yyyy-mm-dd")
    private LocalDate dateBirthFrom;

    @Schema(description = "yyyy-mm-dd")
    private LocalDate dateBirthTo;
}
