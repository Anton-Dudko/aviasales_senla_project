package eu.senla.userservice.service;

import eu.senla.userservice.entity.User;
import eu.senla.userservice.exception.ExceptionMessageConstant;
import eu.senla.userservice.exception.custom.AuthenticatException;
import eu.senla.userservice.exception.custom.NotFoundException;
import eu.senla.userservice.mapper.UserRequestMapper;
import eu.senla.userservice.repository.UserRepository;
import eu.senla.userservice.request.LoginRequest;
import eu.senla.userservice.request.RefreshJwtRequest;
import eu.senla.userservice.request.UserRequest;
import eu.senla.userservice.response.AuthResponse;
import eu.senla.userservice.security.UserDetailsImpl;
import eu.senla.userservice.security.jwt.JwtProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuthService {
    private final JwtProvider jwtProvider;
    private final UserRequestMapper userRequestMapper;
    private final AuthenticationManager authenticationManager;

    private final UserRepository repository;

    private final Map<String, String> refreshStorage = new HashMap<>();

    private final PasswordEncoder passwordEncoder;

    public AuthResponse createUser(UserRequest request) {
        if (repository.findByEmail(request.getEmail()).isEmpty()) {
            User user = userRequestMapper.requestToEntity(request);
            user.setPassword(passwordEncoder.encode(request.getPassword()));
            user = repository.save(user);
            return AuthResponse.builder()
                    .accessToken(generateAccessToken(user))
                    .refreshToken(generateRefreshToken(user))
                    .build();
        } else {
            throw new AuthenticatException(ExceptionMessageConstant.USER_EXIST);
        }
    }

    public AuthResponse authenticateUser(LoginRequest request) {
        User user = repository.findByEmail(request.getEmail())
                .orElseThrow(() -> new NotFoundException(ExceptionMessageConstant.NOT_FOUND_USER));
        UsernamePasswordAuthenticationToken authInputToken =
                new UsernamePasswordAuthenticationToken(user.getUsername(), request.getPassword());
        Authentication authentication = authenticationManager.authenticate(authInputToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        return AuthResponse.builder()
                .accessToken(generateAccessToken(user))
                .refreshToken(generateRefreshToken(user))
                .build();
    }

    public AuthResponse getAccessToken(RefreshJwtRequest request) {
        try {
            User user = getUserFromRefreshToken(request);
            String accessToken = jwtProvider.generateAccessToken(user);
            return new AuthResponse(accessToken, null);
        } catch (AuthenticatException e) {
            return new AuthResponse(null, null);
        }
    }

    public AuthResponse receiveRefreshToken(RefreshJwtRequest request) {
        User user = getUserFromRefreshToken(request);
        return AuthResponse.builder()
                .accessToken(generateAccessToken(user))
                .refreshToken(generateRefreshToken(user))
                .build();
    }

    private User getUserFromRefreshToken(RefreshJwtRequest request) {
        String refreshToken = request.getRefreshToken();
        if (jwtProvider.validateRefreshToken(refreshToken)) {
            String username = jwtProvider.getLoginFromRefreshToken(refreshToken);
            String saveRefreshToken = refreshStorage.get(username);
            if (saveRefreshToken != null && saveRefreshToken.equals(refreshToken)) {
                return repository.findByUsername(username)
                        .orElseThrow(() -> new NotFoundException(ExceptionMessageConstant.NOT_FOUND_USER));
            }
        }
        throw new AuthenticatException(ExceptionMessageConstant.INVALID_TOKEN);
    }

    private String generateAccessToken(User user) {
        return jwtProvider.generateAccessToken(user);
    }

    private String generateRefreshToken(User user) {
        String refreshToken = jwtProvider.generateRefreshToken(user);
        refreshStorage.put(user.getUsername(), refreshToken);
        return refreshToken;
    }

    public UserDetails validateAccessToken(String accessToken) {
        if (jwtProvider.validateAccessToken(accessToken)) {
            String username = jwtProvider.getLoginFromAccessToken(accessToken);
            User user = repository.findByUsername(username)
                    .orElseThrow(() -> new NotFoundException(ExceptionMessageConstant.NOT_FOUND_USER));
            return UserDetailsImpl.fromUserToUserDetails(user);
        }
        throw new AuthenticatException(ExceptionMessageConstant.INVALID_TOKEN);
    }

//    public void resetPassword(String email) {
//        User user = repository.findByEmail(email)
//                .orElseThrow(() -> new NotFoundException(ExceptionMessageConstant.NOT_FOUND_USER));
//        String accessToken = jwtProvider.generateAccessToken(user);
//        String refreshToken = generateRefreshToken(user);
//        refreshStorage.put(user.getUsername(), refreshToken);
//
//        return new GenericResponse(
//                messages.getMessage("message.resetPasswordEmail", null,
//                        request.getLocale()));
//    }

    public int receiveTokenStorageSize() {
        return refreshStorage.values().size();
    }


}