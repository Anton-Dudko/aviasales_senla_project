package io.inter.project.gateway.Filter;

import io.inter.project.gateway.exception.GatewayServiceException;
import io.inter.project.gateway.model.UserDto;
import io.inter.project.gateway.service.FilterService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

import java.util.List;

@Slf4j
@Component
public class AuthFilter extends AbstractGatewayFilterFactory<AuthFilter.Config> {

    private final String uri = "http://userservice";
    private final WebClient.Builder webClientBuilder;

    private final FilterService filterService;

    @Autowired
    public AuthFilter(WebClient.Builder webClientBuilder, FilterService filterService) {
        super(Config.class);
        this.webClientBuilder = webClientBuilder;
        this.filterService = filterService;
    }

    @Override
    public List<String> shortcutFieldOrder() {
        return List.of("isAdminCheck");
    }

    @Override
    public GatewayFilter apply(Config config) {
        log.debug("Start of filtering request");
        return (exchange, chain) -> {
            try {
                String accessToken = filterService.getAuthToken(exchange.getRequest().getHeaders()).trim();
                log.debug("Token value {}", accessToken);

                return filterService.takeUserDetailsFromToken(accessToken)
                        .flatMap(userDto -> {
                            if (config.isAdminCheck() && !userDto.getAuthorities().get(0).getAuthority().contains("ROLE_ADMIN")) {
                                log.warn("No authorities for this");
                                return Mono.error(new ResponseStatusException(HttpStatus.UNAUTHORIZED, "No authorities for this"));
                            }
                            exchange.getRequest().mutate().header("x-auth-user", String.valueOf(userDto));
                            return Mono.just(exchange);
                        })
                        .flatMap(chain::filter);
            } catch (GatewayServiceException e) {
                log.warn("Incorrect auth structure");
                return Mono.error(new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Incorrect auth structure"));
            }
        };
    }

    public static class Config {

        private String isAdminCheck;

        public Config(String isAdminCheck) {
            this.isAdminCheck = isAdminCheck;
        }

        public boolean isAdminCheck() {
            return isAdminCheck.equalsIgnoreCase("true");
        }

    }
}
