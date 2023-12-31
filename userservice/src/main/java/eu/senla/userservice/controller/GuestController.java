package eu.senla.userservice.controller;

import eu.senla.userservice.request.LoginRequest;
import eu.senla.userservice.request.NewPasswordRequest;
import eu.senla.userservice.request.UserRequest;
import eu.senla.userservice.response.AuthResponse;
import eu.senla.userservice.response.TextResponse;
import eu.senla.userservice.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/guest")
public class GuestController {

    private final AuthService authService;

    @PostMapping("/registration")
    public AuthResponse registerUser(@Valid @RequestBody UserRequest request) {
        log.info("...Method registerUser");
        return authService.createUser(request);
    }

    @PostMapping("/login")
    public AuthResponse logIn(@Valid @RequestBody LoginRequest request) {
        log.info("...Method logIn");
        return authService.authenticateUser(request);
    }

    @PostMapping("/new-password")
    public TextResponse generatePassword(@Valid @RequestBody NewPasswordRequest request) {
        log.info("...Method generatePassword");
        return authService.generatePassword(request);
    }
}