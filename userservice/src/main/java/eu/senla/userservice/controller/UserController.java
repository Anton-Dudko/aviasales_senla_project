package eu.senla.userservice.controller;

import eu.senla.common.entity.Role;
import eu.senla.userservice.request.UserRequest;
import eu.senla.userservice.response.UserGetListResponse;
import eu.senla.userservice.response.UserResponse;
import eu.senla.userservice.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.security.RolesAllowed;
import javax.validation.Valid;

@Slf4j
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    @RolesAllowed("ADMIN")
    public UserResponse createAdmin(@Valid @RequestBody UserRequest request) {
        log.trace("Method saveAdmin");
        request.setRole(Role.ROLE_ADMIN.name());
        UserResponse response = userService.save(request);
        log.trace("Response with created admin: {}", response);
        return response;
    }

    @GetMapping("/{id}")
    @RolesAllowed("ROLE_ADMIN")
    public UserResponse findById(@PathVariable Long id) {
        log.trace("Method findById");
        UserResponse response = userService.findById(id);
        log.trace("Response with user: {}", response);
        return response;
    }

    @GetMapping("/page")
    @RolesAllowed("ROLE_ADMIN")
    public UserGetListResponse findAllUsersInPage(@RequestParam(defaultValue = "0") Integer page,
                                                  @RequestParam(defaultValue = "10") Integer size) {
        log.trace("Method findAllUsersInPage");
        Pageable pageable = PageRequest.of(page, size);
        UserGetListResponse responses = userService.findAllByRoleOnPage(pageable, Role.ROLE_USER);
        log.trace("Response with users: {}", responses);
        return responses;
    }

    @GetMapping("/admin/page")
    @RolesAllowed("ROLE_ADMIN")
    public UserGetListResponse findAllAdminInPage(@RequestParam(defaultValue = "0") Integer page,
                                                  @RequestParam(defaultValue = "10") Integer size) {
        log.trace("Method findAllAdminInPage");
        Pageable pageable = PageRequest.of(page, size);
        UserGetListResponse responses = userService.findAllByRoleOnPage(pageable, Role.ROLE_ADMIN);
        log.trace("Response with admins: {}", responses);
        return responses;
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or @userSecurity.hasUserId(authentication,#id)")
    public UserResponse update(@PathVariable Long id, @Valid @RequestBody UserRequest request) {
        UserResponse response = userService.update(id, request);
        log.trace("Response with updated user: {}", response);
        return response;
    }

    @DeleteMapping("/{id}")
    @RolesAllowed("ROLE_ADMIN")
    public void deleteById(@PathVariable Long id) {
        log.trace("Delete user:  id {}", id);
        userService.delete(id);
    }
}