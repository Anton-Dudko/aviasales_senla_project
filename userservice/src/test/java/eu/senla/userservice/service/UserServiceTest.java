package eu.senla.userservice.service;

import eu.senla.common.entity.Role;
import eu.senla.userservice.entity.Language;
import eu.senla.userservice.repository.UserRepository;
import eu.senla.userservice.request.UserFindRequest;
import eu.senla.userservice.request.UserRequest;
import eu.senla.userservice.response.UserGetPageResponse;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Slf4j
@TestPropertySource("/application.yml")
@SpringBootTest
class UserServiceTest {

    @Autowired
    private UserService userService;
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
            .role(ROLE_ADMIN)
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
    void givenUserFindRequest_findBySpecificationRole_returnUserGetPageResponse() {
        int sizePage = 5;
        Pageable pageable = PageRequest.of(0, sizePage);
        Pageable pageable1 = PageRequest.of(1, sizePage);

        initRepository(sizePage);
        authService.createUser(userRequest);
        int countTotal = userRepository.findAll().size();

        UserFindRequest userFindRequestUser = UserFindRequest.builder()
                .role(ROLE_USER)
                .build();

        UserFindRequest userFindRequestAdmin = UserFindRequest.builder()
                .role(ROLE_ADMIN)
                .build();

        UserGetPageResponse users = userService.findBySpecification(userFindRequestUser, pageable);
        UserGetPageResponse admins = userService.findBySpecification(userFindRequestAdmin, pageable);

        assertAll(
                () -> assertEquals(sizePage + 1, countTotal),
                () -> assertEquals(sizePage, users.getUserResponseList().size()),
                () -> assertEquals(1, admins.getUserResponseList().size()),
                () -> assertEquals(0, userService.findBySpecification(userFindRequestAdmin, pageable1).getUserResponseList().size()),
                () -> assertEquals(0, userService.findBySpecification(userFindRequestUser, pageable1).getUserResponseList().size())
        );
    }

    @Test
    void givenUserFindRequest_findBySpecificationLanguage_returnUserGetPageResponse() {
        int sizePage = 5;
        Pageable pageable = PageRequest.of(0, sizePage);
        Pageable pageable1 = PageRequest.of(1, sizePage);

        initRepository(sizePage);
        userRequest.setLanguage(Language.RU.name());
        authService.createUser(userRequest);
        int countTotal = userRepository.findAll().size();

        UserFindRequest userFindRequestEn = UserFindRequest.builder()
                .language(Language.EN.name())
                .build();

        UserFindRequest userFindRequestRu = UserFindRequest.builder()
                .language(Language.RU.name())
                .build();

        UserGetPageResponse en = userService.findBySpecification(userFindRequestEn, pageable);
        UserGetPageResponse ru = userService.findBySpecification(userFindRequestRu, pageable);

        assertAll(
                () -> assertEquals(sizePage + 1, countTotal),
                () -> assertEquals(sizePage, en.getUserResponseList().size()),
                () -> assertEquals(1, ru.getUserResponseList().size()),
                () -> assertEquals(0, userService.findBySpecification(userFindRequestRu, pageable1).getUserResponseList().size()),
                () -> assertEquals(0, userService.findBySpecification(userFindRequestEn, pageable1).getUserResponseList().size())
        );
    }

    private void initRepository(int records) {
        for (int i = 0; i < records; i++) {
            UserRequest request = UserRequest.builder()
                    .username(USERNAME + i)
                    .email(EMAIL + i)
                    .password(PASSWORD + i)
                    .dateBirth(DATE_BIRTH)
                    .build();
            authService.createUser(request);
        }
    }
}