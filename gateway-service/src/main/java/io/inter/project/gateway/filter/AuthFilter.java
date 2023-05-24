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

import java.util.List;

@Slf4j
@Component
public class AuthFilter extends AbstractGatewayFilterFactory<AuthFilter.Config> {

    public static final String PARAM_HEADER_NAME = "x-auth-user";
    public static final String ADMIN_PARAM_VALUE = "ROLE_ADMIN";
    private final FilterService filterService;

    @Autowired
    public AuthFilter(FilterService filterService) {
        super(Config.class);
        this.filterService = filterService;
    }

    @Override
    public List<String> shortcutFieldOrder() {
        return List.of("isAdminCheck");
    }

    @Override
    public GatewayFilter apply(Config config) {
        log.info("Start of filtering request");

        return (exchange, chain) -> {
            try {
                String accessToken = filterService.getAuthToken(exchange.getRequest().getHeaders());
                return filterService.takeUserDetailsFromToken(accessToken)
                        .flatMap(userDto -> {
                            if (config.isAdminCheck() && !userDto.getAuthorities().get(0).getAuthority().contains(ADMIN_PARAM_VALUE)) {
                                log.warn("No authorities for this");
                                return Mono.error(new ResponseStatusException(HttpStatus.UNAUTHORIZED, "No authorities for this"));
                            }
                            exchange.getRequest().mutate().header(PARAM_HEADER_NAME, String.valueOf(userDto));
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
