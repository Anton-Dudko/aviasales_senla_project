package eu.senla.userservice;

import eu.senla.common.entity.Role;
import eu.senla.userservice.repository.UserRepository;
import eu.senla.userservice.request.LoginRequest;
import eu.senla.userservice.request.UserRequest;
import eu.senla.userservice.response.AuthResponse;
import eu.senla.userservice.service.AuthService;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;

import static io.restassured.RestAssured.given;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AuthControllerITest extends ContainerTest {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AuthService authService;
    private final UserRequest userPostRequest = new UserRequest();

    @BeforeEach
    public void before() {
        userRepository.deleteAll();

        userPostRequest.setUsername(TestConstant.USERNAME_FIRST);
        userPostRequest.setPassword(TestConstant.PASSWORD_FIRST);
        userPostRequest.setEmail(TestConstant.EMAIL_FIRST);

        RestAssured.baseURI = "http://localhost:" + port;
        RestAssured.basePath = "/auth/";
    }

    @AfterEach
    public void afterEach() {
        userRepository.deleteAll();
    }

    @Test
    public void givenUserRequestCorrect_whenCreateUser_thenAuthResponse() {
        int tokenStorageSizeBefore = authService.receiveTokenStorageSize();
        int sizeBefore = userRepository.findAllByRole(null, Role.ROLE_USER).getSize();

        AuthResponse response = given()
                .contentType(ContentType.JSON)
                .and()
                .body(userPostRequest)
                .when()
                .post("/signup").then()
                .statusCode(HttpStatus.OK.value())
                .extract().as(AuthResponse.class);
        Long idPostUser = userRepository.findByEmail(userPostRequest.getEmail()).get().getId();
        String emailFact = userRepository.findById(idPostUser).get().getEmail();
        int tokenStorageSizeAfter = authService.receiveTokenStorageSize();

        Assertions.assertNotNull(response.getAccessToken());
        Assertions.assertNotNull(response.getRefreshToken());

        Assertions.assertEquals(sizeBefore + 1, userRepository.findAllByRole(null, Role.ROLE_USER).getSize());
        Assertions.assertEquals(tokenStorageSizeBefore + 1, tokenStorageSizeAfter);
        Assertions.assertEquals(userPostRequest.getEmail(), emailFact);
    }

    @Test
    public void givenUserRequestExistEmail_whenCreateUser_thenCONFLICT() {
        given()
                .contentType(ContentType.JSON)
                .and()
                .body(userPostRequest)
                .when()
                .post("/signup");
        int sizeBefore = userRepository.findAllByRole(null, Role.ROLE_USER).getSize();

        given()
                .contentType(ContentType.JSON)
                .and()
                .body(userPostRequest)
                .when()
                .post("/signup").then()
                .statusCode(HttpStatus.UNAUTHORIZED.value());
        int sizeAfter = userRepository.findAllByRole(null, Role.ROLE_USER).getSize();

        Assertions.assertEquals(sizeBefore, sizeAfter);
    }

    @Test
    public void givenUserRequestNullUsername_whenCreateUser_thenBAD_REQUEST() {
        UserRequest userPostRequest = new UserRequest();
        userPostRequest.setPassword(TestConstant.PASSWORD_FIRST);
        userPostRequest.setEmail(TestConstant.EMAIL_FIRST);

        given()
                .contentType(ContentType.JSON)
                .and()
                .body(userPostRequest)
                .when()
                .post("/signup").then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .extract().response();
    }

    @Test
    public void givenUserRequestNullEmail_whenCreateUser_thenBAD_REQUEST() {
        UserRequest userPostRequest = new UserRequest();
        userPostRequest.setUsername(TestConstant.USERNAME_FIRST);
        userPostRequest.setPassword(TestConstant.PASSWORD_FIRST);

        given()
                .contentType(ContentType.JSON)
                .and()
                .body(userPostRequest)
                .when()
                .post("/signup").then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .extract().response();
    }


    @Test
    public void givenLoginRequestCorrect_whenLoginUser_thenAuthResponce() {
        given()
                .contentType(ContentType.JSON)
                .and()
                .body(userPostRequest)
                .when()
                .post("/signup").then()
                .statusCode(HttpStatus.OK.value());
        LoginRequest request = new LoginRequest();
        request.setEmail(userPostRequest.getEmail());
        request.setPassword(userPostRequest.getPassword());

        AuthResponse response = given()
                .contentType(ContentType.JSON)
                .and()
                .body(request)
                .when()
                .post("/login").then()
                .statusCode(HttpStatus.OK.value())
                .extract().as(AuthResponse.class);

        Assertions.assertNotNull(response.getAccessToken());
    }

    @Test
    public void givenLoginRequestWrongPassword_whenLoginUser_thenUNAUTHORIZED() {
        given()
                .contentType(ContentType.JSON)
                .and()
                .body(userPostRequest)
                .when()
                .post("/signup").then()
                .statusCode(HttpStatus.OK.value());
        LoginRequest request = new LoginRequest();
        request.setEmail(userPostRequest.getEmail());
        request.setPassword(userPostRequest.getPassword() + "1");

        given()
                .contentType(ContentType.JSON)
                .and()
                .body(request)
                .when()
                .post("/login").then()
                .statusCode(HttpStatus.UNAUTHORIZED.value());
    }

    @Test
    public void givenLoginRequestWrongEmail_whenLoginUser_thenNOT_FOUND() {
        given()
                .contentType(ContentType.JSON)
                .and()
                .body(userPostRequest)
                .when()
                .post("/signup").then()
                .statusCode(HttpStatus.OK.value());
        LoginRequest request = new LoginRequest();
        request.setEmail(userPostRequest.getEmail() + "1");
        request.setPassword(userPostRequest.getPassword());

        given()
                .contentType(ContentType.JSON)
                .and()
                .body(request)
                .when()
                .post("/login").then()
                .statusCode(HttpStatus.NOT_FOUND.value());
    }
}
