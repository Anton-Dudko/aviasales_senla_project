package eu.senla.userservice.mapper;

import eu.senla.userservice.entity.User;
import eu.senla.userservice.kafka.UserEvent;
import eu.senla.userservice.request.UserRequest;
import eu.senla.userservice.response.TextResponse;
import eu.senla.userservice.response.UserResponse;

import java.util.List;


public interface UserMapper {

    List<UserResponse> listEntityToListResponse(List<User> all);

    UserResponse entityToResponse(User entity);

    TextResponse entityToTextResponse(User user);

    UserEvent entityToEvent(User user);

    User requestToEntity(UserRequest request);
}
