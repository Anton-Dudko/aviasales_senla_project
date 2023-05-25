package io.inter.project.gateway.filter;

import io.inter.project.gateway.exception.GatewayServiceException;
import io.inter.project.gateway.service.FilterService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class UserIdFilter extends AbstractGatewayFilterFactory<UserIdFilter.Config> {

    private final FilterService filterService;

    @Autowired
    public UserIdFilter(FilterService filterService) {
        super(UserIdFilter.Config.class);
        this.filterService = filterService;
    }

    @Override
    public GatewayFilter apply(Config config) {
        log.info("Start of filtering request");

        return (exchange, chain) -> {
            try {
                String accessToken = filterService.getAuthToken(exchange.getRequest().getHeaders());
                return filterService.takeUserDetailsFromToken(accessToken).flatMap(userDetails -> {
                    int incomingRequestId = extractRequestIdFromPath(exchange.getRequest().getPath().toString());
                    if (incomingRequestId != userDetails.getUserId()) {
                        return Mono.error(new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied"));
                    }
                    return Mono.just(filterService.insertUserDetailsInToResponse(exchange, userDetails));
                }).flatMap(chain::filter);

            } catch (GatewayServiceException e) {
                log.warn("Incorrect auth structure");
                return Mono.error(new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Incorrect auth structure"));
            } catch (NumberFormatException e) {
                return Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid ID format"));
            }
        };
    }

    private int extractRequestIdFromPath(String path) throws NumberFormatException {
        String[] segments = path.split("/");
        return Integer.parseInt(segments[segments.length - 1]);
    }

    public static class Config {}
}