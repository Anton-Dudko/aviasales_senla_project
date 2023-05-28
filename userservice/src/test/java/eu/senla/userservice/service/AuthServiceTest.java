package eu.senla.userservice.service;

import eu.senla.common.entity.Role;
import eu.senla.userservice.entity.Language;
import eu.senla.userservice.exception.ExceptionMessageConstant;
import eu.senla.userservice.exception.custom.AuthenticatException;
import eu.senla.userservice.exception.custom.NotFoundException;
import eu.senla.userservice.repository.UserRepository;
import eu.senla.userservice.request.LoginRequest;
import eu.senla.userservice.request.UserRequest;
import eu.senla.userservice.response.AuthResponse;
import eu.senla.userservice.response.PasswordResponse;
import eu.senla.userservice.response.UserResponse;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@Slf4j
@TestPropertySource("/application.yml")
@SpringBootTest
class AuthServiceTest {

    @Autowired
    private AuthService authService;
    @Autowired
    private UserRepository userRepository;
    private final String USERNAME = "Username";
    private final String EMAIL = "email@mail.ru";
    private final String PASSWORD = "password";
    private final String ROLE_ADMIN = Role.ROLE_ADMIN.name();
    private final String ROLE_USER = Role.ROLE_USER.name();
    private final String LANGUAGE = Language.EN.name();
    private final String DATE_BIRTH = "2000-12-12";

    UserRequest userRequest = UserRequest.builder()
            .username(USERNAME)
            .email(EMAIL)
            .password(PASSWORD)
            .dateBirth(DATE_BIRTH)
            .language(LANGUAGE)
            .build();

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }

    @AfterEach
    void tearDown() {
        userRepository.deleteAll();
    }

    @Test
    void givenUserRequest_createUser_thenReturnAuthResponse() {
        int sizeBefore = userRepository.findAll().size();

        AuthResponse authResponse = authService.createUser(userRequest);
        int sizeAfter = userRepository.findAll().size();

        assertAll(
                () -> assertEquals(sizeBefore + 1, sizeAfter),
                () -> assertNotNull(userRepository.findByEmail(userRequest.getEmail())),
                () -> assertNotNull(userRepository.findByUsername(userRequest.getUsername())),
                () -> assertEquals(ROLE_USER, userRepository.findByEmail(EMAIL).get().getRole().name()),
                () -> assertNotNull(authResponse.getRefreshToken()),
                () -> assertNotNull(authResponse.getAccessToken())
        );
    }

    @Test
    void givenUserRequest_createUserExistEmail_thenAuthenticateException() {
        authService.createUser(userRequest);
        int sizeBefore = userRepository.findAll().size();

        Exception exception = Assertions.assertThrows(AuthenticatException.class, () -> {
            authService.createUser(userRequest);
        });

        String expectedMessage = ExceptionMessageConstant.USER_EXIST;
        String actualMessage = exception.getMessage();

        Assertions.assertTrue(actualMessage.contains(expectedMessage));
        int sizeAfter = userRepository.findAll().size();

        Assertions.assertEquals(sizeBefore, sizeAfter);
    }

    @Test
    void givenUserRequest_createAdmin_thenReturnAuthResponse() {
        int sizeBefore = userRepository.findAll().size();

        AuthResponse authResponse = authService.createAdmin(userRequest);
        int sizeAfter = userRepository.findAll().size();

        assertAll(
                () -> assertEquals(sizeBefore + 1, sizeAfter),
                () -> assertNotNull(userRepository.findByEmail(userRequest.getEmail())),
                () -> assertNotNull(userRepository.findByUsername(userRequest.getUsername())),
                () -> assertEquals(ROLE_ADMIN, userRepository.findByEmail(EMAIL).get().getRole().name()),
                () -> assertNotNull(authResponse.getRefreshToken()),
                () -> assertNotNull(authResponse.getAccessToken())
        );
    }

    @Test
    void givenLoginRequest_authenticateUser_returnAuthResponse() {
        authService.createUser(userRequest);

        AuthResponse responce = authService.authenticateUser(LoginRequest.builder()
                .email(EMAIL)
                .password(PASSWORD)
                .build());
        System.out.println(userRepository.findByEmail(EMAIL).get().getPassword());

        Assertions.assertAll(
                () -> assertNotNull(responce.getRefreshToken()),
                () -> assertNotNull(responce.getAccessToken())
        );
    }

    @Test
    void givenLoginRequest_authenticateUserNotExistEmail_returnNotFoundException() {
        Exception exception = Assertions.assertThrows(NotFoundException.class, () -> {
            authService.authenticateUser(LoginRequest.builder()
                    .email(EMAIL + "1")
                    .password(PASSWORD)
                    .build());
        });

        String expectedMessage = ExceptionMessageConstant.NOT_FOUND_USER;
        String actualMessage = exception.getMessage();

        Assertions.assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void givenLoginRequest_authenticateUserWrongPassword_returnAuthenticatException() {
        authService.createUser(userRequest);
        Exception exception = Assertions.assertThrows(AuthenticatException.class, () -> {
            authService.authenticateUser(LoginRequest.builder()
                    .email(EMAIL)
                    .password(PASSWORD + "1")
                    .build());
        });

        String expectedMessage = ExceptionMessageConstant.INVALID_PASSWORD;
        String actualMessage = exception.getMessage();

        Assertions.assertTrue(actualMessage.contains(expectedMessage));
    }


    @Test
    void givenAccessToken_validateAccessToken_returnUserResponse() {
        String accessToken = authService.createUser(userRequest).getAccessToken();
        UserResponse response = authService.validateAccessToken(accessToken);
        assertAll(
                () -> assertEquals(USERNAME, response.getUsername()),
                () -> assertEquals(EMAIL, response.getEmail()),
                () -> assertEquals(LANGUAGE, response.getLanguage()),
                () -> assertEquals(DATE_BIRTH, response.getDateBirth())
        );
    }

    @Test
    void givenAccessToken_validateAccessTokenNotExistUsername_returnNotFoundException() {
        String token = authService.createUser(userRequest).getAccessToken();
        userRepository.delete(userRepository.findByEmail(EMAIL).get());
        Exception exception = Assertions.assertThrows(NotFoundException.class, () -> {
            authService.validateAccessToken(token);
        });

        String expectedMessage = ExceptionMessageConstant.NOT_FOUND_USER;
        String actualMessage = exception.getMessage();

        Assertions.assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void givenAccessToken_validateAccessTokenNotValidate_returnAuthenticatException() {
        String token = authService.createUser(userRequest).getAccessToken() + "z";
        Exception exception = Assertions.assertThrows(AuthenticatException.class, () -> {
            authService.validateAccessToken(token);
        });

        String expectedMessage = ExceptionMessageConstant.INVALID_TOKEN;
        String actualMessage = exception.getMessage();

        Assertions.assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void givenEmail_generatePassword_returnPasswordResponse() {
        authService.createUser(userRequest);
        PasswordResponse response = authService.generatePassword(EMAIL);
        AuthResponse authResponse = authService.authenticateUser(LoginRequest.builder()
                .email(EMAIL)
                .password(response.getPassword())
                .build());

        assertAll(
                () -> assertEquals(USERNAME, response.getUsername()),
                () -> assertEquals(EMAIL, response.getEmail()),
                () -> assertNotNull(response.getPassword()),
                () -> assertNotNull(userRepository.findByEmail(EMAIL).get().getPassword()),
                () -> assertNotNull(authResponse.getAccessToken()),
                () -> assertNotNull(authResponse.getRefreshToken())
        );

    }

    @Test
    void givenEmail_generatePasswordNotExistEmail_returnNotFoundException() {
        Exception exception = Assertions.assertThrows(NotFoundException.class, () -> {
            authService.generatePassword(EMAIL);
        });

        String expectedMessage = ExceptionMessageConstant.USER_NOT_REGISTRATE;
        String actualMessage = exception.getMessage();

        Assertions.assertTrue(actualMessage.contains(expectedMessage));
    }
}