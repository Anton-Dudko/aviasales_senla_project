package com.aviasales.finance.dto;

import com.aviasales.finance.enums.UserRole;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserDetailsDto {
    private Long userId;
    private String username;
    private String email;
    private String language;
    private UserRole role;
}
