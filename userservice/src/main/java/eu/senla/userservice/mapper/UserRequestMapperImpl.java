package eu.senla.userservice.mapper;

import eu.senla.common.entity.Role;
import eu.senla.userservice.entity.Language;
import eu.senla.userservice.entity.User;
import eu.senla.userservice.request.UserRequest;
import eu.senla.userservice.response.UserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;


@Component
@RequiredArgsConstructor
public class UserRequestMapperImpl implements UserRequestMapper {

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
    public User requestToEntity(UserRequest request) {
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setDateBirth(request.getDateBirth());
        user.setLanguage(request.getLanguage() != null
                ? Language.valueOf(request.getLanguage())
                : Language.EN);
        user.setRole(request.getRole() != null
                ? Role.valueOf(request.getRole())
                : Role.valueOf("ROLE_USER"));
        return user;
    }

}
