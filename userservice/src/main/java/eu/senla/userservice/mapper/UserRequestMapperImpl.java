package eu.senla.userservice.mapper;

import eu.senla.common.entity.Role;
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
                .build();
    }

    @Override
    public User requestToEntity(UserRequest request) {
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        if (request.getRole() != null) {
            user.setRole(Role.valueOf(request.getRole()));
        } else {
            user.setRole(Role.valueOf("ROLE_USER"));
        }
        return user;
    }

}
