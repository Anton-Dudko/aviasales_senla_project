package eu.senla.userservice.service;

import eu.senla.common.entity.Role;
import eu.senla.userservice.entity.User;
import eu.senla.userservice.exception.ExceptionMessageConstant;
import eu.senla.userservice.exception.custom.NotFoundException;
import eu.senla.userservice.mapper.UserRequestMapper;
import eu.senla.userservice.repository.UserRepository;
import eu.senla.userservice.request.UserRequest;
import eu.senla.userservice.response.UserGetListResponse;
import eu.senla.userservice.response.UserResponse;
import eu.senla.userservice.token.PasswordCoder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserRequestMapper userMapper;


    public UserGetListResponse findAllByRoleOnPage(Pageable pageable, Role role) {
        Page<User> pagedResult = userRepository.findAllByRole(pageable, role);

        return pagedResult.hasContent()
                ? UserGetListResponse.builder()
                .userResponseList(userMapper.listEntityToListResponse(pagedResult.getContent()))
                .build()
                : UserGetListResponse.builder()
                .userResponseList(new ArrayList<>())
                .build();
    }

    public UserResponse findById(Long id) {
        log.trace("Method findById");
        return userMapper.entityToResponse(userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(ExceptionMessageConstant.NOT_FOUND_USER)));
    }

    public UserResponse update(Long id, UserRequest request) {
        log.trace("Method update");
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(ExceptionMessageConstant.NOT_FOUND_USER));
        user.setUsername(request.getUsername());
//        user.setPassword(passwordEncoder.encode(request.getPassword()));
        User updatedUser = userRepository.save(user);
        return userMapper.entityToResponse(updatedUser);
    }


    public void delete(Long id) {
        log.trace("Method delete");
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(ExceptionMessageConstant.NOT_FOUND_USER));
        userRepository.delete(user);
    }
}
