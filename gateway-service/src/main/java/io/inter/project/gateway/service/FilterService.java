package io.inter.project.gateway.service;

import io.inter.project.gateway.exception.GatewayServiceException;

import io.inter.project.gateway.request.UserDetails;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Objects;
import java.util.Optional;

@Slf4j
@Service
public class FilterService {

    public static final String AUTH_TOKEN = "Authorization";
    private static final String uri = "http://userservice/auth/validate";
    private final WebClient.Builder webClientBuilder;

    @Autowired
    public FilterService(WebClient.Builder webClientBuilder) {
        this.webClientBuilder = webClientBuilder;
    }

    public String getAuthToken(HttpHeaders requestHeaders) throws GatewayServiceException {
        log.debug("Start of extracting token from request");
        if (requestHeaders.get(AUTH_TOKEN) != null) {
            Optional<String> authRow = Objects.requireNonNull(requestHeaders.get(AUTH_TOKEN)).stream().findFirst();
            if (authRow.isPresent()) {
                return authRow.get().replace("Bearer ", "");
            } else {
                log.warn("An error occurred while processing token");
                throw new GatewayServiceException("An error occurred while processing token");
            }

        } else {
            log.warn("Request doesn't have correct token");
            throw new GatewayServiceException("Request doesn't have correct token");
        }
    }

    public Mono<UserDetails> takeUserDetailsFromToken(String accessToken) {
        log.debug("Token value {}", accessToken);

        return webClientBuilder.build().get()
                .uri(String.format("%s?accessToken=%s", uri, accessToken))
                .retrieve()
                .onStatus(HttpStatus::is4xxClientError, response -> {
                    log.warn("Token not found or expired");
                    return Mono.error(new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token not found or expired"));
                })
                .bodyToMono(UserDetails.class);
    }

    public ServerWebExchange insertUserDetailsInToResponse(ServerWebExchange exchange, UserDetails userDetails) {
        exchange.mutate()
                .request(builder -> builder
                        .header("id", userDetails.getUserId().toString())
                        .header("username", userDetails.getUsername())
                        .header("email", userDetails.getEmail())
                        .header("role", userDetails.getRole())
                        .header("dateBirth", userDetails.getDateBirth() != null ?
                                userDetails.getDateBirth().toString() : null)
                        .header("language", userDetails.getLanguage() != null ?
                                userDetails.getLanguage() : null)
                )
                .build();

        return exchange;
    }

}
