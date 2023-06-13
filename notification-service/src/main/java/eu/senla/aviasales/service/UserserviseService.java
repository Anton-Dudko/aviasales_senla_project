package eu.senla.aviasales.service;

import eu.senla.aviasales.exception.ExceptionMessageConstants;
import eu.senla.aviasales.exception.custom.UserServiceException;
import eu.senla.aviasales.response.UserGetListResponse;
import eu.senla.aviasales.response.UserResponse;
import eu.senla.aviasales.service.constant.UserserserviceConstant;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserserviseService {
    private final RestTemplate restTemplate;


    public List<UserResponse> getAllAdminsInfo() {
        log.info("... method getAllAdminEmail");
        UriComponentsBuilder builder = UriComponentsBuilder
                .fromUriString(UserserserviceConstant.USER_SERVICE_URL)
                .pathSegment(UserserserviceConstant.SEARCH_ADMIN_EMAIL);
        try {
            ResponseEntity<UserGetListResponse> response = restTemplate.exchange(
                    builder.toUriString(),
                    HttpMethod.GET,
                    new HttpEntity<>(new HttpHeaders()),
                    new ParameterizedTypeReference<>() {
                    });
            return Objects.requireNonNull(response.getBody()).getUserResponseList();

        } catch (HttpClientErrorException | HttpServerErrorException e) {
            log.warn(e.getMessage());
            throw new UserServiceException(ExceptionMessageConstants.EXCEPTION_FROM_USERSERVICE);
        }
    }

    public UserResponse getUserByEmail(String email) {
        log.info("... method getUserByEmail");
        UriComponentsBuilder builder = UriComponentsBuilder
                .fromUriString(UserserserviceConstant.USER_SERVICE_URL)
                .pathSegment(UserserserviceConstant.SEARCH_USER_BY_EMAIL)
                .queryParam("email", email);
        try {
            ResponseEntity<UserResponse> response = restTemplate.exchange(
                    builder.toUriString(),
                    HttpMethod.GET,
                    new HttpEntity<>(new HttpHeaders()),
                    new ParameterizedTypeReference<>() {
                    });
            return Objects.requireNonNull(response.getBody());

        } catch (HttpClientErrorException | HttpServerErrorException e) {
            log.warn(e.getMessage());
            throw new UserServiceException(ExceptionMessageConstants.EXCEPTION_FROM_USERSERVICE);
        }
    }
}
