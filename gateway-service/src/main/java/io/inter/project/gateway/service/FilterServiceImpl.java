package io.inter.project.gateway.service;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.inter.project.gateway.exception.GatewayServiceException;

import io.inter.project.gateway.request.UserDetails;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
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
public class FilterServiceImpl implements FilterService{

    public static final String AUTH_TOKEN = "Authorization";
    public static final String VALIDATE_URI = "http://userservice/auth/validate";
    public static final String URI_TOKEN_PARAM = "%s?accessToken=%s";
    public static final String USER_DETAILS_HEADER_PARAM = "userDetails";
    private final WebClient.Builder webClientBuilder;

    @Autowired
    public FilterServiceImpl(WebClient.Builder webClientBuilder) {
        this.webClientBuilder = webClientBuilder;
    }

    @Override
    public String getAuthToken(HttpHeaders requestHeaders) throws GatewayServiceException {
        log.debug("Start of extracting token from request");

        if (requestHeaders.containsKey(AUTH_TOKEN)) {
            String authRow = Objects.requireNonNull(requestHeaders.get(AUTH_TOKEN)).stream()
                    .findFirst()
                    .orElse("");
            if (StringUtils.isNotBlank(authRow)) {
                return authRow.replace("Bearer ", "");
            } else {
                log.warn("An error occurred while processing token");
                throw new GatewayServiceException("An error occurred while processing token");
            }
        } else {
            log.warn("Request doesn't have correct token");
            throw new GatewayServiceException("Request doesn't have correct token");
        }
    }

    @Override
    public Mono<UserDetails> takeUserDetailsFromToken(String accessToken) {
        log.debug("Token value {}", accessToken);

        return webClientBuilder.build().get()
                .uri(String.format(URI_TOKEN_PARAM, VALIDATE_URI, accessToken))
                .retrieve()
                .onStatus(HttpStatus::is4xxClientError, response -> {
                    log.warn("Token not found or expired");
                    return Mono.error(new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token not found or expired"));
                })
                .bodyToMono(UserDetails.class);
    }

    @Override
    public ServerWebExchange insertUserDetailsInToResponse(ServerWebExchange exchange,
                                                           UserDetails userDetails) throws GatewayServiceException {
        ObjectMapper objectMapper = new ObjectMapper()
                .setSerializationInclusion(JsonInclude.Include.ALWAYS)
                .registerModule(new JavaTimeModule());

        try {
            String userDetailsJson = objectMapper.writeValueAsString(userDetails);
            if (StringUtils.isNotBlank(userDetailsJson)) {
                exchange.mutate()
                        .request(builder -> builder
                                .header(USER_DETAILS_HEADER_PARAM, userDetailsJson)
                        )
                        .build();
            } else {
                log.warn("User details JSON is null or empty");
                throw new GatewayServiceException("User details JSON is null or empty");
            }
        } catch (JsonProcessingException e) {
            log.warn("An error occurred while processing entity", e);
            throw new GatewayServiceException("An error occurred while processing entity");
        }

        return exchange;
    }

}
