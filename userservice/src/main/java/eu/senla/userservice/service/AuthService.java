package eu.senla.userservice.service;

import eu.senla.common.enam.Role;
import eu.senla.userservice.entity.User;
import eu.senla.userservice.exception.ExceptionMessageConstants;
import eu.senla.userservice.exception.custom.AuthenticatException;
import eu.senla.userservice.exception.custom.NotFoundException;
import eu.senla.userservice.kafka.KafkaTopicConstants;
import eu.senla.userservice.mapper.UserMapper;
import eu.senla.userservice.repository.UserRepository;
import eu.senla.userservice.request.LoginRequest;
import eu.senla.userservice.request.UserRequest;
import eu.senla.userservice.response.AuthResponse;
import eu.senla.userservice.response.PasswordResponse;
import eu.senla.userservice.response.UserResponse;
import eu.senla.userservice.security.jwt.JwtProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.RandomStringUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;


@Slf4j
@Component
@RequiredArgsConstructor
public class AuthService {
    private final JwtProvider jwtProvider;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final UserRepository repository;
    private final KafkaService kafkaService;

    public AuthResponse createUser(UserRequest request) {
        log.info("...method createUser");
        if (repository.findByUsername(request.getUsername()).isPresent()) {
            throw new AuthenticatException(ExceptionMessageConstants.USER_WITH_SUCH_USERNAME_EXIST);
        }
        if (repository.findByEmail(request.getEmail()).isPresent()) {
            throw new AuthenticatException(ExceptionMessageConstants.USER_WITH_SUCH_EMAIL_EXIST);
        }
        User user = userMapper.requestToEntity(request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setAccessToken(generateAccessToken(user));
        user.setRefreshToken(generateRefreshToken(user));
        user = repository.save(user);

        kafkaService.sendToTopic(KafkaTopicConstants.REGISTERED_EVENT, user);

        return AuthResponse.builder()
                .accessToken(user.getAccessToken())
                .refreshToken(user.getRefreshToken())
                .build();
    }

    public AuthResponse createAdmin(UserRequest request) {
        request.setRole(Role.ROLE_ADMIN.name());
        return createUser(request);

    }

    public AuthResponse authenticateUser(LoginRequest request) {
        User user = repository.findByEmail(request.getEmail())
                .orElseThrow(() -> new NotFoundException(ExceptionMessageConstants.USER_NOT_FOUND));
        if (passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            user.setAccessToken(generateAccessToken(user));
            user.setRefreshToken(generateRefreshToken(user));
            user = repository.save(user);
            return AuthResponse.builder()
                    .accessToken(user.getAccessToken())
                    .refreshToken(user.getRefreshToken())
                    .build();
        } else {
            throw new AuthenticatException(ExceptionMessageConstants.INVALID_PASSWORD);
        }
    }

    public UserResponse validateAccessToken(String accessToken) {
        String refreshToken = repository.findByAccessToken(accessToken)
                .orElseThrow(() -> new NotFoundException(ExceptionMessageConstants.USER_NOT_FOUND))
                .getRefreshToken();

        if (jwtProvider.validateAccessToken(accessToken)) {
            log.info("...access token is validate");
            User user = repository.findByUsername(jwtProvider.getLoginFromAccessToken(accessToken))
                    .orElseThrow(() -> new NotFoundException(ExceptionMessageConstants.USER_NOT_FOUND));
            return userMapper.entityToResponse(user);

        } else if (jwtProvider.validateRefreshToken(refreshToken)) {
            log.info("...refresh token is validate");
            User user = repository.findByUsername(jwtProvider.getLoginFromRefreshToken(refreshToken))
                    .orElseThrow(() -> new NotFoundException(ExceptionMessageConstants.USER_NOT_FOUND));
            user.setAccessToken(generateAccessToken(user));
            user = repository.save(user);
            return userMapper.entityToResponse(user);
        }
        log.info("...all tokens are invalid");
        throw new AuthenticatException(ExceptionMessageConstants.INVALID_TOKEN);
    }


    public PasswordResponse generatePassword(String email) {
        User user = repository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException(ExceptionMessageConstants.USER_NOT_REGISTRATE));
        user.setAccessToken(generateAccessToken(user));
        user.setRefreshToken(generateRefreshToken(user));
        String password = RandomStringUtils.randomNumeric(email.length());
        user.setPassword(passwordEncoder.encode(password));
        user = repository.save(user);

        kafkaService.sendToTopic(KafkaTopicConstants.RESET_PASSWORD_EVENT, user, password);

        return PasswordResponse.builder()
                .password(password)
                .email(user.getEmail())
                .username(user.getUsername())
                .userId(user.getId())
                .build();
    }

    private String generateAccessToken(User user) {
        log.info("method generateAccessToken");
        return jwtProvider.generateAccessToken(user);
    }

    private String generateRefreshToken(User user) {
        return jwtProvider.generateRefreshToken(user);
    }
}
