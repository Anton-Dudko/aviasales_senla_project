package eu.senla.userservice.controller;

import eu.senla.userservice.response.UserResponse;
import eu.senla.userservice.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;


@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class TokenController {

    private final AuthService authService;

    @GetMapping("/validate")
    public UserResponse validateAccessToken(@Valid @NotNull String accessToken) {
        log.info("...Method validateAccessToken");
        return authService.validateAccessToken(accessToken);
    }
}
