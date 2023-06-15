package eu.senla.userservice.service;

import eu.senla.common.enam.Role;
import eu.senla.userservice.dao.UserSpecification;
import eu.senla.userservice.entity.User;
import eu.senla.userservice.exception.ExceptionMessageConstants;
import eu.senla.userservice.exception.custom.AuthenticatException;
import eu.senla.userservice.exception.custom.NotFoundException;
import eu.senla.userservice.mapper.UserMapper;
import eu.senla.userservice.repository.UserRepository;
import eu.senla.userservice.request.UserFindRequest;
import eu.senla.userservice.request.UserUpdateRequest;
import eu.senla.userservice.response.TextResponse;
import eu.senla.userservice.response.TextResponseMessageConstants;
import eu.senla.userservice.response.UserGetPageResponse;
import eu.senla.userservice.response.UserResponse;
import eu.senla.userservice.service.constant.KafkaTopicConstants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Validated
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final EnumValidator enumValidator;
    private final PasswordEncoder passwordEncoder;
    private final KafkaService kafkaService;

    public UserGetPageResponse findBySpecification(@Valid UserFindRequest request,
                                                   @NotNull Pageable pageable) {
        log.info("Method findBySpecification");
        enumValidator.checkLanguageEnum(request.getLanguage());
        enumValidator.checkRoleEnum(request.getRole());
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

    public UserResponse findById(@NotNull Long id) {
        log.info("Method findById");
        return userMapper.entityToResponse(userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(ExceptionMessageConstants.USER_NOT_FOUND)));
    }

    public UserGetPageResponse findAllAdmin() {
        log.info("Method findAllAdminEmail");
        List<User> listAdmin = userRepository.findAllByRole(Role.ROLE_ADMIN);
        return UserGetPageResponse.builder()
                .userResponseList(userMapper.listEntityToListResponse(listAdmin))
                .count(listAdmin.size())
                .build();
    }

    public UserResponse update(@NotNull Long id,
                               @Valid UserUpdateRequest request) {
        log.info("Method update");
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new NotFoundException(ExceptionMessageConstants.USER_NOT_FOUND));
        if (id.equals(user.getId())) {
            if (StringUtils.isNotEmpty(request.getUsername())
                    && !request.getUsername().equals(user.getUsername())
                    && userRepository.findByUsername(request.getUsername()).isPresent()) {
                throw new AuthenticatException(ExceptionMessageConstants.USER_WITH_SUCH_USERNAME_EXIST);
            }
            user.setUsername(StringUtils.isNotEmpty(request.getUsername())
                    ? request.getUsername()
                    : user.getUsername());
            user.setPassword(StringUtils.isNotEmpty(request.getPassword())
                    ? passwordEncoder.encode(request.getPassword())
                    : user.getPassword());
            user.setDateBirth(request.getDateBirth() != null
                    ? request.getDateBirth()
                    : user.getDateBirth());
            user.setLanguage(enumValidator.checkLanguageEnum(request.getLanguage()));
            User updatedUser = userRepository.save(user);

            if (request.getPassword() != null) {
                kafkaService.sendToTopic(KafkaTopicConstants.UPDATE_PASSWORD_EVENT, user, request.getPassword());
            }
            return userMapper.entityToResponse(updatedUser);
        }
        throw new AuthenticatException(ExceptionMessageConstants.EMAIL_DONT_MATCH_ID);
    }


    public TextResponse delete(@NotNull Long id) {
        log.info("Method delete");
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(ExceptionMessageConstants.USER_NOT_FOUND));
        userRepository.delete(user);
        return TextResponse.builder()
                .message(TextResponseMessageConstants.PASSWORD_SENT_TO_EMAIL)
                .username(user.getUsername())
                .email(user.getEmail())
                .build();
    }

    public UserResponse findByEmail(String email) {
        log.info("Method findByEmail");
        return userMapper.entityToResponse(userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException(ExceptionMessageConstants.USER_NOT_FOUND)));
    }
}
