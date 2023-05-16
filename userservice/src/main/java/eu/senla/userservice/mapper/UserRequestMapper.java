package eu.senla.userservice.mapper;

import eu.senla.userservice.entity.User;
import eu.senla.userservice.request.UserRequest;
import eu.senla.userservice.response.UserResponse;

import java.util.List;


public interface UserRequestMapper {

    List<UserResponse> listEntityToListResponse(List<User> all);

    UserResponse entityToResponse(User entity);

    User requestToEntity(UserRequest request);
}
