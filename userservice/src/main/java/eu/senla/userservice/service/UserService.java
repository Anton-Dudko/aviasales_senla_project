package eu.senla.userservice.service;


import eu.senla.common.enam.Language;
import eu.senla.userservice.dao.UserSpecification;
import eu.senla.userservice.entity.User;
import eu.senla.userservice.exception.ExceptionMessageConstants;
import eu.senla.userservice.exception.custom.AuthenticatException;
import eu.senla.userservice.exception.custom.NotFoundException;
import eu.senla.userservice.kafka.KafkaTopicConstants;
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
import org.apache.commons.lang.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final KafkaService kafkaService;

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
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new NotFoundException(ExceptionMessageConstants.USER_NOT_FOUND));
        if (id.equals(user.getId())) {
            user.setUsername(StringUtils.isNotEmpty(request.getUsername())
                    ? request.getUsername()
                    : user.getUsername());
            user.setPassword(StringUtils.isNotEmpty(request.getPassword())
                    ? passwordEncoder.encode(request.getPassword())
                    : user.getPassword());
            user.setDateBirth(StringUtils.isNotEmpty(request.getDateBirth())
                    ? LocalDate.parse(request.getDateBirth())
                    : user.getDateBirth());
            user.setLanguage(StringUtils.isNotEmpty(request.getLanguage())
                    ? Language.valueOf(request.getLanguage())
                    : user.getLanguage());
            User updatedUser = userRepository.save(user);

            if (request.getPassword() != null) {
                kafkaService.sendToTopic(KafkaTopicConstants.UPDATE_PASSWORD_EVENT, user, request.getPassword());
            }
            return userMapper.entityToResponse(updatedUser);
        }
        throw new AuthenticatException(ExceptionMessageConstants.EMAIL_DONT_MATCH_ID);
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
