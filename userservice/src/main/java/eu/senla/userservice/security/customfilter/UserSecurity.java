package eu.senla.userservice.security.customfilter;

import eu.senla.userservice.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserSecurity {

    private final UserService userService;

    public boolean hasUserId(Authentication authentication, Long userId) {
        String name = authentication.getName();
        return userService.findById(userId).getUsername().equals(name);
    }
}
