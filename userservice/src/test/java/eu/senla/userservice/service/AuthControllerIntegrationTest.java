package eu.senla.userservice.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@Slf4j
@TestPropertySource("/application.yml")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AuthControllerIntegrationTest {

//    @LocalServerPort
//    private int port;
//    @Autowired
//    JdbcTemplate jdbcTemplate;
//
//    RestTemplate restTemplate;
//    private final String USERNAME = "Username";
//    private final String EMAIL = "email@mail.ru";
//    private final String PASSWORD = "password";
//    private final String ROLE_ADMIN = "ROLE_ADMIN";
//
//    UserRequest user = UserRequest.builder()
//            .username(USERNAME)
//            .email(EMAIL)
//            .password(PASSWORD)
//            .build();
//
//    String baseUrl;
//
//
//    @BeforeEach
//    void setUp() {
//        baseUrl = "http://localhost:" + port + "/guest";
//        restTemplate = new RestTemplate();
//        log.info("application started with base URL:{} and port:{}", baseUrl, port);
//
//    }
//
//    @AfterEach
//    void tearDown() {
//    }
//
//    @Test
//    void givenUserRequest_whenRegistrations_thenReturnAuthResponse() {
//        user.setRole(ROLE_ADMIN);
//        ResponseEntity<AuthResponse> authResponse = restTemplate.postForEntity(baseUrl, user, AuthResponse.class);
//        assertAll(
//                () -> assertNotNull(authResponse.getBody()),
//                () -> assertNotNull(authResponse.getBody().getAccessToken()),
//                () -> assertNotNull(authResponse.getBody().getRefreshToken())
//        );
//    }
//
//    @Test
//    void authenticateUser() {
//    }
//
//    @Test
//    void getAccessToken() {
//    }
//
//    @Test
//    void receiveRefreshToken() {
//    }
//
//    @Test
//    void validateAccessToken() {
//    }
//
//    @Test
//    void receiveTokenStorageSize() {
//    }
}