package eu.senla.userservice.controller;

import eu.senla.userservice.request.RefreshJwtRequest;
import eu.senla.userservice.response.AuthResponse;
import eu.senla.userservice.response.UserResponse;
import eu.senla.userservice.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class TokenController {

    private final AuthService authService;

    @PostMapping("/refreshtoken")
    public AuthResponse receiveRefreshToken(@Valid @RequestBody RefreshJwtRequest request) {
        log.trace("Method getNewRefreshToken");
        AuthResponse response = authService.receiveRefreshToken(request);
        log.trace("Response authenticate: {}", response);
        return response;
    }

    @GetMapping("/validate")
    public UserResponse validateAccessToken(String accessToken) {
        log.trace("Method validateAccessToken");
        return authService.validateAccessToken(accessToken);
    }
}
