package eu.senla.userservice.controller;

import eu.senla.userservice.request.LoginRequest;
import eu.senla.userservice.request.UserRequest;
import eu.senla.userservice.response.AuthResponse;
import eu.senla.userservice.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.constraints.Email;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping
public class AuthController {

    private final AuthService authService;

    @PostMapping("/guest")
    public AuthResponse registerUser(@Valid @RequestBody UserRequest request) {
        log.trace("Method registrations");
        AuthResponse response = authService.createUser(request);
        log.trace("Response: {}", response);
        return response;
    }

    @PostMapping("/guest/login")
    public AuthResponse logIn(@Valid @RequestBody LoginRequest request) {
        log.trace("Method logIn");
        AuthResponse response = authService.authenticateUser(request);
        log.trace("Response: {}", response);
        return response;
    }

    @PostMapping("/guest/password")
    public PasswordResponse generatePassword(@Valid @Email String email) {
        log.trace("Method generatePassword");
        PasswordResponse response = authService.generatePassword(email);
        log.trace("Response : {}", response);
        return response;
    }

    @PostMapping("/admin")
    public AuthResponse registerAdmin(@Valid @RequestBody UserRequest request) {
        log.trace("Method registerAdmin");
        AuthResponse response = authService.createAdmin(request);
        log.trace("Response with created admin: {}", response);
        return response;
    }
}
