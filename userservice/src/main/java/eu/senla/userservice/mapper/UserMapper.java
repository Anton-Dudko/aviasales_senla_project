package eu.senla.userservice.mapper;

import eu.senla.userservice.entity.User;
import eu.senla.userservice.kafka.UserEvent;
import eu.senla.userservice.request.UserRequest;
import eu.senla.userservice.response.TextResponse;
import eu.senla.userservice.response.UserResponse;

import javax.validation.constraints.NotNull;
import java.util.List;


public interface UserMapper {

    List<UserResponse> listEntityToListResponse(List<User> all);

    UserResponse entityToResponse(@NotNull User entity);

    TextResponse entityToTextResponse(@NotNull User user);

    UserEvent entityToEvent(@NotNull User user);

    User requestToEntity(@NotNull UserRequest request);
}
