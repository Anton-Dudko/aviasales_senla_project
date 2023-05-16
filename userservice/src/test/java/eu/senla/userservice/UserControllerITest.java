package eu.senla.userservice;

import eu.senla.userservice.entity.Role;
import eu.senla.userservice.entity.User;
import eu.senla.userservice.repository.UserRepository;
import eu.senla.userservice.request.UserRequest;
import eu.senla.userservice.response.UserGetListResponse;
import eu.senla.userservice.response.UserResponse;
import eu.senla.userservice.service.AuthService;
import eu.senla.userservice.service.UserService;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;

import static eu.senla.userservice.TestConstant.EMAIL_FIRST;
import static eu.senla.userservice.TestConstant.EMAIL_SECOND;
import static eu.senla.userservice.TestConstant.PASSWORD_FIRST;
import static eu.senla.userservice.TestConstant.PASSWORD_SECOND;
import static eu.senla.userservice.TestConstant.ROLE_ADMIN;
import static eu.senla.userservice.TestConstant.ROLE_USER;
import static eu.senla.userservice.TestConstant.USERNAME_SECOND;
import static io.restassured.RestAssured.given;

import static eu.senla.userservice.TestConstant.USERNAME_FIRST;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UserControllerITest extends ContainerTest {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AuthService authService;
    @Autowired
    private UserService userService;

    private String token;
    private Long id;

    @BeforeEach
    public void before() {
        userRepository.deleteAll();
        token = receiveTokenFromAuthorisateUser(ROLE_ADMIN);
        RestAssured.baseURI = "http://localhost:" + port;
        RestAssured.basePath = "/users/";

    }

    @AfterEach
    public void afterEach() {
        userRepository.deleteAll();
    }

    @Test
    public void givenUserId_whenFindByIdDontAuthorized_thenUNAUTHORIZED() {
        given()
                .contentType(ContentType.JSON)
                .pathParam("id", id)
                .when()
                .get("/{id}").then()
                .statusCode(HttpStatus.UNAUTHORIZED.value());
    }

    @Test
    public void givenUserId_whenFindByIdAuthorized_thenUserResponse() {
        UserResponse response = given()
                .header("Authorization", "Bearer " + token)
                .contentType(ContentType.JSON)
                .pathParam("id", id)
                .and()
                .get("/{id}").then()
                .statusCode(HttpStatus.OK.value())
                .extract().as(UserResponse.class);
        UserResponse factResponce = userService.findById(id);

        Assertions.assertEquals(id, response.getUserId());
        Assertions.assertEquals(factResponce.getUsername(), response.getUsername());
        Assertions.assertEquals(factResponce.getEmail(), response.getEmail());
    }

    @Test
    public void givenUserId_whenFindByIdWrongRole_thenFORBIDDEN() {
        userRepository.deleteAll();

        String wrongToken = receiveTokenFromAuthorisateUser(ROLE_USER);

        given()
                .header("Authorization", "Bearer " + wrongToken)
                .contentType(ContentType.JSON)
                .pathParam("id", id)
                .and()
                .get("/{id}").then()
                .statusCode(HttpStatus.FORBIDDEN.value());
    }

    @Test
    public void givenUserIdNotExist_whenFindById_thenNOT_FOUND() {
        Long idNotExist = -1L;
        given()
                .header("Authorization", "Bearer " + token)
                .contentType(ContentType.JSON)
                .pathParam("id", idNotExist)
                .when()
                .get("/{id}").then()
                .statusCode(HttpStatus.NOT_FOUND.value());
    }

    @Test
    public void whenFindAllUsersInPage_thenUNAUTHORIZED() {
        given()
                .contentType(ContentType.JSON)
                .when()
                .get("/page").then()
                .statusCode(HttpStatus.UNAUTHORIZED.value());
    }

    @Test
    public void whenFindAllUsersInPage_thenUserGetListResponse() {
        userService.save(new UserRequest("1", "1@tut.by", "1", ROLE_USER));
        userService.save(new UserRequest("2", "2@tut.by", "2", ROLE_USER));
        userService.save(new UserRequest("3", "3@tut.by", "3", ROLE_USER));
        userService.save(new UserRequest("4", "4@tut.by", "4", ROLE_ADMIN));
        Pageable pageable = PageRequest.of(0, 10);
        long size = userRepository.findAllByRole(pageable, Role.ROLE_USER).getContent().size();

        UserGetListResponse response = given()
                .header("Authorization", "Bearer " + token)
                .contentType(ContentType.JSON)
                .when()
                .get("/page").then()
                .statusCode(HttpStatus.OK.value())
                .extract().as(UserGetListResponse.class);
        Assertions.assertEquals(size, response.getUserResponseList().size());
    }

    @Test
    public void whenFindAllUsersInPageWrongRole_thenFORBIDDEN() {
        userRepository.deleteAll();
        String wrongToken = receiveTokenFromAuthorisateUser(ROLE_USER);

        given()
                .header("Authorization", "Bearer " + wrongToken)
                .contentType(ContentType.JSON)
                .when()
                .get("/page").then()
                .statusCode(HttpStatus.FORBIDDEN.value());
    }

    @Test
    public void whenFindAllAdminsInPageDontAuthorized_thenUNAUTHORIZED() {
        given()
                .contentType(ContentType.JSON)
                .when()
                .get("/admin/page").then()
                .statusCode(HttpStatus.UNAUTHORIZED.value());
    }

    @Test
    public void whenFindAllAdminsInPage_thenUserGetListResponse() {
        userService.save(new UserRequest("1", "1@tut.by", "1", ROLE_ADMIN));
        userService.save(new UserRequest("2", "2@tut.by", "2", ROLE_USER));
        userService.save(new UserRequest("3", "3@tut.by", "3", ROLE_USER));
        userService.save(new UserRequest("4", "4@tut.by", "4", ROLE_ADMIN));
        Pageable pageable = PageRequest.of(0, 10);
        long size = userRepository.findAllByRole(pageable, Role.ROLE_ADMIN).getContent().size();

        UserGetListResponse response = given()
                .header("Authorization", "Bearer " + token)
                .contentType(ContentType.JSON)
                .when()
                .get("/admin/page").then()
                .statusCode(HttpStatus.OK.value())
                .extract().as(UserGetListResponse.class);
        Assertions.assertEquals(size, response.getUserResponseList().size());
    }

    @Test
    public void whenFindAllAdminsInPageWrongRole_thenFORBIDDEN() {
        userRepository.deleteAll();
        String wrongToken = receiveTokenFromAuthorisateUser(ROLE_USER);

        given()
                .header("Authorization", "Bearer " + wrongToken)
                .contentType(ContentType.JSON)
                .when()
                .get("/admin/page").then()
                .statusCode(HttpStatus.FORBIDDEN.value());
    }

    @Test
    public void givenUserId_whenDeleteByIdDontAuthorized_thenUNAUTHORIZED() {
        given()
                .contentType(ContentType.JSON)
                .pathParam("id", id)
                .when()
                .delete("/{id}").then()
                .statusCode(HttpStatus.UNAUTHORIZED.value());
    }

    @Test
    public void givenUserId_whenDeleteByIdAuthorized_thenOK() {

        given()
                .header("Authorization", "Bearer " + token)
                .contentType(ContentType.JSON)
                .pathParam("id", id)
                .when()
                .delete("/{id}").then()
                .statusCode(HttpStatus.OK.value())
                .extract().response();
    }

    @Test
    public void givenUserId_whenDeleteByIdWrongRole_thenFORBIDDEN() {
        userRepository.deleteAll();
        String wrongToken = receiveTokenFromAuthorisateUser(ROLE_USER);

        given()
                .header("Authorization", "Bearer " + wrongToken)
                .contentType(ContentType.JSON)
                .pathParam("id", id)
                .and()
                .delete("/{id}").then()
                .statusCode(HttpStatus.FORBIDDEN.value());
    }

    @Test
    public void givenUserIdNotExist_whenDeleteById_thenNOT_FOUND() {
        Long idNotExist = -1L;
        given()
                .header("Authorization", "Bearer " + token)
                .contentType(ContentType.JSON)
                .pathParam("id", idNotExist)
                .when()
                .delete("/{id}").then()
                .statusCode(HttpStatus.NOT_FOUND.value());
    }

    @Test
    public void whenCreateAdminDontAuthorized_thenUNAUTHORIZED() {
        UserRequest userPostRequest = new UserRequest();
        userPostRequest.setUsername(USERNAME_SECOND);
        userPostRequest.setPassword(PASSWORD_SECOND);
        userPostRequest.setEmail(EMAIL_SECOND);
        userPostRequest.setRole(ROLE_ADMIN);

        given()
                .contentType(ContentType.JSON)
                .and()
                .body(userPostRequest)
                .when()
                .post().then()
                .statusCode(HttpStatus.UNAUTHORIZED.value());
    }


    @Test
    public void givenUserRequest_whenCreateAdminAuthorized_thenOK() {
        Pageable pageable = PageRequest.of(0, 10);
        int sizeBeforeAdd = userRepository.findAllByRole(pageable, Role.ROLE_ADMIN).getContent().size();
        UserRequest userPostRequest = new UserRequest();
        userPostRequest.setUsername(USERNAME_SECOND);
        userPostRequest.setPassword(PASSWORD_SECOND);
        userPostRequest.setEmail(EMAIL_SECOND);
        userPostRequest.setRole(ROLE_ADMIN);

        given()
                .header("Authorization", "Bearer " + token)
                .contentType(ContentType.JSON)
                .and()
                .body(userPostRequest)
                .when()
                .post().then()
                .statusCode(HttpStatus.OK.value());
        int sizeAfterAdd = userRepository.findAllByRole(pageable, Role.ROLE_ADMIN).getContent().size();
        Long idPostUser = userRepository.findByEmail(userPostRequest.getEmail()).get().getId();

        UserResponse responseGet = given()
                .header("Authorization", "Bearer " + token)
                .contentType(ContentType.JSON)
                .pathParam("id", idPostUser)
                .when()
                .get("/{id}").then()
                .statusCode(HttpStatus.OK.value())
                .extract().as(UserResponse.class);


        Assertions.assertEquals(userPostRequest.getEmail(), responseGet.getEmail());
        Assertions.assertEquals(idPostUser, responseGet.getUserId());
        Assertions.assertEquals(sizeBeforeAdd+1, sizeAfterAdd);

    }

    @Test
    public void givenUserRequest_whenCreateAdminWrongRole_thenFORBIDDEN() {
        userRepository.deleteAll();
        UserRequest userPostRequest = new UserRequest();
        userPostRequest.setUsername(USERNAME_SECOND);
        userPostRequest.setPassword(PASSWORD_SECOND);
        userPostRequest.setEmail(EMAIL_SECOND);
        userPostRequest.setRole(ROLE_ADMIN);

        String wrongToken = receiveTokenFromAuthorisateUser(ROLE_USER);

        given()
                .header("Authorization", "Bearer " + wrongToken)
                .contentType(ContentType.JSON)
                .and()
                .body(userPostRequest)
                .when()
                .post().then()
                .statusCode(HttpStatus.FORBIDDEN.value());
    }

    @Test
    public void givenUserRequestNullUsername_whenCreateAdmin_thenBAD_REQUEST() {
        UserRequest userPostRequest = new UserRequest();
        userPostRequest.setPassword(PASSWORD_SECOND);
        userPostRequest.setEmail(EMAIL_SECOND);
        userPostRequest.setRole(ROLE_ADMIN);

        given()
                .header("Authorization", "Bearer " + token)
                .contentType(ContentType.JSON)
                .and()
                .body(userPostRequest)
                .when()
                .post().then()
                .statusCode(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    public void givenUserRequestNullEmail_whenCreateAdmin_thenBAD_REQUEST() {
        UserRequest userPostRequest = new UserRequest();
        userPostRequest.setUsername(USERNAME_SECOND);
        userPostRequest.setPassword(PASSWORD_SECOND);
        userPostRequest.setRole(ROLE_ADMIN);

        given()
                .header("Authorization", "Bearer " + token)
                .contentType(ContentType.JSON)
                .and()
                .body(userPostRequest)
                .when()
                .post().then()
                .statusCode(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    public void whenUpdateDontAuthorized_thenUNAUTHORIZED() {
        User userBefore = userRepository.findById(id).get();
        UserRequest userPutRequest = new UserRequest();
        userPutRequest.setUsername(USERNAME_SECOND);
        userPutRequest.setEmail(userBefore.getEmail());
        userPutRequest.setPassword(PASSWORD_SECOND);

        given()
                .contentType(ContentType.JSON)
                .pathParam("id", id)
                .body(userPutRequest)
                .when()
                .put("/{id}").then()
                .statusCode(HttpStatus.UNAUTHORIZED.value());
    }

    @Test
    public void givenUserRequest_whenUpdateCorrectRole_thenOK() {
        User userBefore = userRepository.findById(id).get();
        UserRequest userPutRequest = new UserRequest();
        userPutRequest.setUsername(USERNAME_SECOND);
        userPutRequest.setEmail(userBefore.getEmail());
        userPutRequest.setPassword(PASSWORD_SECOND);

        given()
                .header("Authorization", "Bearer " + token)
                .contentType(ContentType.JSON)
                .pathParam("id", id)
                .body(userPutRequest)
                .when()
                .put("/{id}").then()
                .statusCode(HttpStatus.OK.value());

        User userAfter = userRepository.findById(id).get();

        Assertions.assertNotEquals(userBefore.getUsername(), userAfter.getUsername());
        Assertions.assertEquals(userBefore.getEmail(), userAfter.getEmail());
        Assertions.assertNotEquals(userBefore.getPassword(), userAfter.getPassword());
        Assertions.assertEquals(userBefore.getId(), userBefore.getId());

        Assertions.assertEquals(USERNAME_SECOND, userAfter.getUsername());
    }

    @Test
    public void givenUserRequest_whenUpdateWrongRole_thenFORBIDDEN() {
        userRepository.deleteAll();
        String wrongToken = receiveTokenFromAuthorisateUser(ROLE_USER);
        User userBefore = userRepository.findById(id).get();
        UserRequest userPutRequest = new UserRequest();
        userPutRequest.setUsername(USERNAME_SECOND);
        userPutRequest.setEmail(userBefore.getEmail());
        userPutRequest.setPassword(PASSWORD_SECOND);

        given()
                .header("Authorization", "Bearer " + wrongToken)
                .contentType(ContentType.JSON)
                .pathParam("id", id)
                .body(userPutRequest)
                .when()
                .put("/{id}").then()
                .statusCode(HttpStatus.FORBIDDEN.value());
    }

    @Test
    public void givenUserRequestIdNotExist_whenUpdate_thenNOT_FOUND() {
        Long idNotExist = -1L;
        User userBefore = userRepository.findById(id).get();
        UserRequest userPutRequest = new UserRequest();
        userPutRequest.setUsername(USERNAME_SECOND);
        userPutRequest.setEmail(userBefore.getEmail());
        userPutRequest.setPassword(PASSWORD_SECOND);

        given()
                .header("Authorization", "Bearer " + token)
                .contentType(ContentType.JSON)
               .pathParam("id", idNotExist)
                .body(userPutRequest)
                .when()
                .put("/{id}").then()
                .statusCode(HttpStatus.NOT_FOUND.value());
    }

    private String receiveTokenFromAuthorisateUser(String role) {
        UserRequest signupRequest = new UserRequest();
        signupRequest.setUsername(USERNAME_FIRST);
        signupRequest.setPassword(PASSWORD_FIRST);
        signupRequest.setRole(role);
        signupRequest.setEmail(EMAIL_FIRST);

        String token = authService.createUser(signupRequest).getAccessToken();
        id = userRepository.findByEmail(signupRequest.getEmail()).get().getId();
        return token;
    }
}