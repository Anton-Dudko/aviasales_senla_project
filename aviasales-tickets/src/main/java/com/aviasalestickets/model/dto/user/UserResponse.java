package com.aviasalestickets.model.dto.user;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserResponse {
    private String username;
    private String email;
    private LocalDate dateBirth;
    private String language;
}
