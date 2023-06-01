package eu.senla.userservice.mapper;

import eu.senla.common.enam.Language;
import eu.senla.common.enam.Role;
import eu.senla.userservice.entity.User;
import eu.senla.userservice.kafka.UserEvent;
import eu.senla.userservice.request.UserRequest;
import eu.senla.userservice.response.TextResponse;
import eu.senla.userservice.response.UserResponse;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;


@Component
public class UserMapperImpl implements UserMapper {

    @Override
    public List<UserResponse> listEntityToListResponse(List<User> all) {
        return all.stream()
                .map(this::entityToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public UserResponse entityToResponse(User entity) {
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
    public TextResponse entityToTextResponse(User user) {
        return TextResponse.builder()
                .username(user.getUsername())
                .email(user.getEmail())
                .build();
    }

    @Override
    public User requestToEntity(UserRequest request) {
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setDateBirth(LocalDate.parse(request.getDateBirth()));
        user.setLanguage(request.getLanguage() != null
                ? Language.valueOf(request.getLanguage())
                : Language.EN);
        user.setRole(request.getRole() != null
                ? Role.valueOf(request.getRole())
                : Role.valueOf("ROLE_USER"));
        return user;
    }

    @Override
    public UserEvent entityToEvent(User user) {
        return UserEvent.builder()
                .userName(user.getUsername())
                .userLanguage(user.getLanguage().name().toLowerCase())
                .email(user.getEmail())
                .build();
    }
}
