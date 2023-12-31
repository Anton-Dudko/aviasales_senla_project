package eu.senla.userservice.service;

import eu.senla.common.enam.Language;
import eu.senla.common.enam.Role;
import eu.senla.userservice.exception.ExceptionMessageConstants;
import eu.senla.userservice.exception.custom.AuthenticatException;
import eu.senla.userservice.exception.custom.NotFoundException;
import eu.senla.userservice.repository.UserRepository;
import eu.senla.userservice.request.LoginRequest;
import eu.senla.userservice.request.NewPasswordRequest;
import eu.senla.userservice.request.UserRequest;
import eu.senla.userservice.response.AuthResponse;
import eu.senla.userservice.response.UserResponse;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDate;

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
    private final LocalDate DATE_BIRTH = LocalDate.of(2000, 12, 12);

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
        userRequest.setUsername("new uniq name");

        Exception exception = Assertions.assertThrows(AuthenticatException.class, () -> authService.createUser(userRequest));

        String expectedMessage = ExceptionMessageConstants.USER_WITH_SUCH_EMAIL_EXIST;
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

        AuthResponse responce = authService.authenticateUser(new LoginRequest(EMAIL, PASSWORD));

        Assertions.assertAll(
                () -> assertNotNull(responce.getRefreshToken()),
                () -> assertNotNull(responce.getAccessToken())
        );
    }

    @Test
    void givenLoginRequest_authenticateUserNotExistEmail_returnNotFoundException() {
        Exception exception = Assertions.assertThrows(NotFoundException.class, () -> authService.authenticateUser(new LoginRequest(EMAIL + 1, PASSWORD)));

        String expectedMessage = ExceptionMessageConstants.USER_NOT_FOUND;
        String actualMessage = exception.getMessage();

        Assertions.assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void givenLoginRequest_authenticateUserWrongPassword_returnAuthenticatException() {
        authService.createUser(userRequest);
        Exception exception = Assertions.assertThrows(AuthenticatException.class, () -> authService.authenticateUser(new LoginRequest(EMAIL, PASSWORD)));

        String expectedMessage = ExceptionMessageConstants.INVALID_PASSWORD;
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
                () -> assertEquals(DATE_BIRTH, response.getDateBirth().toString())
        );
    }

    @Test
    void givenAccessToken_validateAccessTokenNotExistUsername_returnNotFoundException() {
        String token = authService.createUser(userRequest).getAccessToken();
        userRepository.delete(userRepository.findByEmail(EMAIL).get());
        Exception exception = Assertions.assertThrows(NotFoundException.class, () -> authService.validateAccessToken(token));

        String expectedMessage = ExceptionMessageConstants.USER_NOT_FOUND;
        String actualMessage = exception.getMessage();

        Assertions.assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void givenAccessToken_validateAccessTokenNotValidate_returnNotFoundException() {
        String token = authService.createUser(userRequest).getAccessToken() + "z";
        Exception exception = Assertions.assertThrows(NotFoundException.class, () -> authService.validateAccessToken(token));

        String expectedMessage = ExceptionMessageConstants.USER_NOT_FOUND;
        String actualMessage = exception.getMessage();

        Assertions.assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void givenEmail_generatePasswordNotExistEmail_returnNotFoundException() {
        Exception exception = Assertions.assertThrows(NotFoundException.class, () -> authService.generatePassword(new NewPasswordRequest(EMAIL)));

        String expectedMessage = ExceptionMessageConstants.USER_NOT_REGISTRATE;
        String actualMessage = exception.getMessage();

        Assertions.assertTrue(actualMessage.contains(expectedMessage));
    }
}