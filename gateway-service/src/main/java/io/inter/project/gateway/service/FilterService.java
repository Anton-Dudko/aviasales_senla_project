package io.inter.project.gateway.service;

import io.inter.project.gateway.exception.GatewayServiceException;
import io.inter.project.gateway.request.UserDetails;
import org.springframework.http.HttpHeaders;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

public interface FilterService {

    String getAuthToken(HttpHeaders requestHeaders) throws GatewayServiceException;
    Mono<UserDetails> takeUserDetailsFromToken(String accessToken);
    ServerWebExchange insertUserDetailsInToResponse(ServerWebExchange exchange, UserDetails userDetails) throws GatewayServiceException;

}
