package io.inter.project.gateway.request;

import lombok.*;

import java.time.LocalDate;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class UserDetails {

    private Long userId;
    private String username;
    private String email;
    private LocalDate dateBirth;
    private String language;
    private String role;

}
