package eu.senla.userservice.mapper;

import eu.senla.userservice.entity.User;
import eu.senla.userservice.exception.ExceptionMessageConstants;
import eu.senla.userservice.kafka.UserEvent;
import eu.senla.userservice.request.UserRequest;
import eu.senla.userservice.response.TextResponse;
import eu.senla.userservice.response.UserResponse;
import eu.senla.userservice.service.EnumValidator;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;


@Component
@RequiredArgsConstructor
public class UserMapperImpl implements UserMapper {

    private final EnumValidator enumValidator;

    @Override
    public List<UserResponse> listEntityToListResponse(List<User> all) {
        return all.stream()
                .map(this::entityToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public UserResponse entityToResponse(@NotNull User entity) {
        return UserResponse.builder()
                .userId(entity.getId())
                .username(entity.getUsername())
                .email(entity.getEmail())
                .dateBirth(entity.getDateBirth())
                .language(entity.getLanguage().name())
                .role(entity.getRole().name())
                .build();
    }

    @Override
    public TextResponse entityToTextResponse(@NotNull User user) {
        return TextResponse.builder()
                .username(user.getUsername())
                .email(user.getEmail())
                .build();
    }

    @Override
    public User requestToEntity(@NotNull UserRequest request) {
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());

        if (StringUtils.isNotEmpty(request.getDateBirth())) {
            user.setDateBirth(LocalDate.parse(request.getDateBirth()));
        } else {
            throw new IllegalArgumentException(ExceptionMessageConstants.DATE_IS_NULL);
        }
        user.setLanguage(enumValidator.checkLanguageEnum(request.getLanguage()));
        user.setRole(enumValidator.checkRoleEnum(request.getRole()));
        return user;
    }

    @Override
    public UserEvent entityToEvent(@NotNull User user) {
        return UserEvent.builder()
                .userName(user.getUsername())
                .userLanguage(user.getLanguage().name().toLowerCase())
                .email(user.getEmail())
                .build();
    }
}
