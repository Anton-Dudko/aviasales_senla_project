package com.aviasalestickets.model.dto.user;

import lombok.Data;

import java.time.LocalDate;

@Data
public class UserDetails {
    private Long userId;
    private String username;
    private String email;
    private LocalDate dateBirth;
    private String language;
    private String role;
}
