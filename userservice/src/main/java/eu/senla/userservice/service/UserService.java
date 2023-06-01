package eu.senla.userservice.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import eu.senla.common.enam.Language;
import eu.senla.userservice.entity.User;
import eu.senla.userservice.exception.ExceptionMessageConstants;
import eu.senla.userservice.exception.custom.NotFoundException;
import eu.senla.userservice.kafka.KafkaTopicConstants;
import eu.senla.userservice.kafka.UserEvent;
import eu.senla.userservice.mapper.UserMapper;
import eu.senla.userservice.repository.UserRepository;
import eu.senla.userservice.request.UserFindRequest;
import eu.senla.userservice.request.UserUpdateRequest;
import eu.senla.userservice.response.TextResponse;
import eu.senla.userservice.response.TextResponseMessageConstants;
import eu.senla.userservice.response.UserGetPageResponse;
import eu.senla.userservice.response.UserResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final Producer<String, Map<String, UserEvent>> producer;
    private final ObjectMapper objectMapper;

    public UserGetPageResponse findBySpecification(UserFindRequest request, Pageable pageable) {
        log.info("Method findBySpecification");
        Page<User> pagedResult = userRepository.findAll(new UserSpecification(request), pageable);
        return pagedResult.hasContent()

                ? UserGetPageResponse.builder()
                .userResponseList(userMapper.listEntityToListResponse(pagedResult.getContent()))
                .count(pagedResult.getContent().size())
                .build()

                : UserGetPageResponse.builder()
                .userResponseList(new ArrayList<>())
                .count(0)
                .build();
    }

    public UserResponse findById(Long id) {
        log.info("Method findById");
        return userMapper.entityToResponse(userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(ExceptionMessageConstants.USER_NOT_FOUND)));
    }

    public UserResponse update(Long id, UserUpdateRequest request) {
        log.info("Method update");
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(ExceptionMessageConstants.USER_NOT_FOUND));
        user.setUsername(request.getUsername() != null
                ? request.getUsername()
                : user.getUsername());
        user.setPassword(request.getPassword() != null
                ? passwordEncoder.encode(request.getPassword())
                : user.getPassword());
        user.setDateBirth(request.getDateBirth() != null
                ? LocalDate.parse(request.getDateBirth())
                : user.getDateBirth());
        user.setLanguage(request.getLanguage() != null
                ? Language.valueOf(request.getLanguage())
                : user.getLanguage());
        User updatedUser = userRepository.save(user);

        if (request.getPassword() != null) {
            UserEvent event = UserEvent.builder()
                    .userName(updatedUser.getUsername())
                    .userLanguage(updatedUser.getLanguage().name().toLowerCase())
                    .email(updatedUser.getEmail())
                    .newPassword(request.getPassword())
                    .build();

            log.info("event ... {}", event);
            ProducerRecord<String, Map<String, UserEvent>> producerRecord =
                    new ProducerRecord<>(KafkaTopicConstants.UPDATE_PASSWORD_EVENT, objectMapper.convertValue(event, Map.class));
            log.info("producerRecord ... {}", producerRecord);
            producer.send(producerRecord);
            log.info("Sending message ... {}", producerRecord);
        }
        return userMapper.entityToResponse(updatedUser);
    }


    public TextResponse delete(Long id) {
        log.info("Method delete");
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(ExceptionMessageConstants.USER_NOT_FOUND));
        userRepository.delete(user);
        TextResponse textResponse = userMapper.entityToTextResponse(user);
        textResponse.setMessage(TextResponseMessageConstants.USER_DELETED);
        return textResponse;
    }
}
