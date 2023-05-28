package eu.senla.userservice.service;

import eu.senla.userservice.entity.Language;
import eu.senla.userservice.entity.User;
import eu.senla.userservice.exception.ExceptionMessageConstant;
import eu.senla.userservice.exception.custom.NotFoundException;
import eu.senla.userservice.mapper.UserRequestMapper;
import eu.senla.userservice.repository.UserRepository;
import eu.senla.userservice.request.UserFindRequest;
import eu.senla.userservice.request.UserRequest;
import eu.senla.userservice.response.UserGetPageResponse;
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
                .orElseThrow(() -> new NotFoundException(ExceptionMessageConstant.NOT_FOUND_USER)));
    }

    public UserResponse update(Long id, UserRequest request) {
        log.info("Method update");
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(ExceptionMessageConstant.NOT_FOUND_USER));
        user.setUsername(request.getUsername());
        user.setPassword(PasswordCoder.codingPassword(request.getPassword()));
        user.setDateBirth(request.getDateBirth() != null
                ? request.getDateBirth()
                : user.getDateBirth());
        user.setLanguage(request.getLanguage() != null
                ? Language.valueOf(request.getLanguage())
                : user.getLanguage());

        User updatedUser = userRepository.save(user);
        return userMapper.entityToResponse(updatedUser);
    }


    public void delete(Long id) {
        log.info("Method delete");
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(ExceptionMessageConstant.NOT_FOUND_USER));
        userRepository.delete(user);
    }
}
