package eu.senla.userservice.controller;

import eu.senla.userservice.request.LoginRequest;
import eu.senla.userservice.request.UserRequest;
import eu.senla.userservice.response.AuthResponse;
import eu.senla.userservice.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    public AuthResponse signUp(@Valid @RequestBody UserRequest request) {
        log.trace("Method signUp");
        AuthResponse response = authService.createUser(request);
        log.trace("Response sign in: {}", response);
        return response;
    }

    @PostMapping("/login")
    public AuthResponse logIn(@Valid @RequestBody LoginRequest request) {
        log.trace("Method logIn");
        AuthResponse response = authService.authenticateUser(request);
        log.trace("Response authenticate: {}", response);
        return response;
    }

    @GetMapping("/validate")
    public UserDetails validateAccessToken(@RequestParam("accessToken") String accessToken) {
        log.trace("Method validateAccessToken {}", accessToken);
        return authService.validateAccessToken(accessToken);
    }
}
