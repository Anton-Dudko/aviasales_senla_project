package io.inter.project.gateway.model;

import lombok.*;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class UserDto {

    private String username;
    private String password;
    private boolean enabled;
    private List<Authority> authorities;
    private boolean accountNonLocked;
    private boolean accountNonExpired;
    private boolean credentialsNonExpired;

}
