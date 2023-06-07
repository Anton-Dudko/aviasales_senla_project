package eu.senla.userservice.controller;

import eu.senla.userservice.request.UserFindRequest;
import eu.senla.userservice.request.UserRequest;
import eu.senla.userservice.response.AuthResponse;
import eu.senla.userservice.response.TextResponse;
import eu.senla.userservice.response.UserGetPageResponse;
import eu.senla.userservice.response.UserResponse;
import eu.senla.userservice.service.AuthService;
import eu.senla.userservice.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminController {

    private final AuthService authService;
    private final UserService userService;

    @PostMapping("/registration")
    public AuthResponse registerAdmin(@Valid @RequestBody UserRequest request) {
        log.info("...Method registerAdmin");
        return authService.createAdmin(request);
    }

    @GetMapping("/users/{id}")
    public UserResponse findById(@PathVariable Long id) {
        log.info("...Method findById");
        return userService.findById(id);
    }

    @GetMapping("/search-admin-email")
    public UserGetPageResponse findAllAdminEmail() {
        log.info("...Method findAllAdminEmail");
        return userService.findAllAdmin();
    }

    @PostMapping("/search")
    public UserGetPageResponse findBySpecification(@RequestParam(defaultValue = "0") Integer page,
                                                   @RequestParam(defaultValue = "10") Integer size,
                                                   @RequestBody UserFindRequest request) {
        log.info("...Method findBySpecification");
        Pageable pageable = PageRequest.of(page, size);
        return userService.findBySpecification(request, pageable);
    }

    @DeleteMapping("/{id}")
    public TextResponse deleteById(@PathVariable Long id) {
        log.info("...Delete user:  id {}", id);
        return userService.delete(id);
    }
}
