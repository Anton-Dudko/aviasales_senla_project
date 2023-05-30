package eu.senla.userservice.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import eu.senla.common.entity.Role;
import eu.senla.userservice.entity.User;
import eu.senla.userservice.exception.ExceptionMessageConstant;
import eu.senla.userservice.exception.custom.AuthenticatException;
import eu.senla.userservice.exception.custom.NotFoundException;
import eu.senla.userservice.kafka.KafkaConstant;
import eu.senla.userservice.kafka.UserEvent;
import eu.senla.userservice.mapper.UserRequestMapper;
import eu.senla.userservice.repository.UserRepository;
import eu.senla.userservice.request.LoginRequest;
import eu.senla.userservice.request.UserRequest;
import eu.senla.userservice.response.AuthResponse;
import eu.senla.userservice.response.PasswordResponse;
import eu.senla.userservice.response.UserResponse;
import eu.senla.userservice.token.jwt.JwtProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuthService {
    private final JwtProvider jwtProvider;
    private final PasswordEncoder passwordEncoder;
    private final UserRequestMapper userRequestMapper;
    private final UserRepository repository;

    private final Producer<String, Map<String, UserEvent>> producer;
    private final ObjectMapper objectMapper;

    public AuthResponse createUser(UserRequest request) {
        if (repository.findByEmail(request.getEmail()).isEmpty()) {
            User user = userRequestMapper.requestToEntity(request);
            user.setPassword(passwordEncoder.encode(request.getPassword()));
            user.setAccessToken(generateAccessToken(user));
            user.setRefreshToken(generateRefreshToken(user));
            user = repository.save(user);

            UserEvent event = UserEvent.builder()
                    .userName(user.getUsername())
                    .userLanguage(user.getLanguage().name().toLowerCase())
                    .email(user.getEmail())
                    .build();
            log.info("event ... {}", event);
            ProducerRecord<String, Map<String, UserEvent>> producerRecord =
                    new ProducerRecord<>(KafkaConstant.REGISTERED_EVENT, objectMapper.convertValue(event, Map.class));
            log.info("producerRecord ... {}", producerRecord);
            producer.send(producerRecord);
            log.info("Sending message ... {}", producerRecord);

            return AuthResponse.builder()
                    .accessToken(user.getAccessToken())
                    .refreshToken(user.getRefreshToken())
                    .build();
        } else {
            throw new AuthenticatException(ExceptionMessageConstant.USER_EXIST);
        }
    }

    public AuthResponse createAdmin(UserRequest request) {
        request.setRole(Role.ROLE_ADMIN.name());
        return createUser(request);

    }

    public AuthResponse authenticateUser(LoginRequest request) {
        User user = repository.findByEmail(request.getEmail())
                .orElseThrow(() -> new NotFoundException(ExceptionMessageConstant.NOT_FOUND_USER));
        if (passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            user.setAccessToken(generateAccessToken(user));
            user.setRefreshToken(generateRefreshToken(user));
            user = repository.save(user);
            return AuthResponse.builder()
                    .accessToken(user.getAccessToken())
                    .refreshToken(user.getRefreshToken())
                    .build();
        } else {
            throw new AuthenticatException(ExceptionMessageConstant.INVALID_PASSWORD);
        }
    }

    public UserResponse validateAccessToken(String accessToken) {
        if (jwtProvider.validateAccessToken(accessToken)) {
            String username = jwtProvider.getLoginFromAccessToken(accessToken);
            User user = repository.findByUsername(username)
                    .orElseThrow(() -> new NotFoundException(ExceptionMessageConstant.NOT_FOUND_USER));
            return UserResponse.builder()
                    .userId(user.getId())
                    .email(user.getEmail())
                    .username(user.getUsername())
                    .dateBirth(user.getDateBirth())
                    .language(user.getLanguage().name())
                    .role(user.getRole().name())
                    .build();
        }
        throw new AuthenticatException(ExceptionMessageConstant.INVALID_TOKEN);
    }


    public PasswordResponse generatePassword(String email) {
        User user = repository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException(ExceptionMessageConstant.USER_NOT_REGISTRATE));
        user.setAccessToken(generateAccessToken(user));
        user.setRefreshToken(generateRefreshToken(user));
        String password = RandomStringUtils.randomAscii(email.length());
        user.setPassword(passwordEncoder.encode(password));
        user = repository.save(user);

        UserEvent event = UserEvent.builder()
                .userName(user.getUsername())
                .userLanguage(user.getLanguage().name().toLowerCase())
                .email(user.getEmail())
                .newPassword(password)
                .build();

        log.info("event ... {}", event);
        ProducerRecord<String, Map<String, UserEvent>> producerRecord =
                new ProducerRecord<>(KafkaConstant.RESET_PASSWORD_EVENT, objectMapper.convertValue(event, Map.class));
        log.info("producerRecord ... {}", producerRecord);
        producer.send(producerRecord);
        log.info("Sending message ... {}", producerRecord);

        return PasswordResponse.builder()
                .password(password)
                .email(user.getEmail())
                .username(user.getUsername())
                .userId(user.getId())
                .build();
    }

    private String generateAccessToken(User user) {
        return jwtProvider.generateAccessToken(user);
    }

    private String generateRefreshToken(User user) {
        return jwtProvider.generateRefreshToken(user);
    }
}
