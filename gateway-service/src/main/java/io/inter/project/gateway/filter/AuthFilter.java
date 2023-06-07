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

    public static final String ADMIN_PARAM_VALUE = "ROLE_ADMIN";
    public static final String CONFIG_SHORTCUT = "isAdminCheck";
    private final FilterService filterService;

    @Autowired
    public AuthFilter(FilterService filterService) {
        super(Config.class);
        this.filterService = filterService;
    }

    @Override
    public List<String> shortcutFieldOrder() {
        return List.of(CONFIG_SHORTCUT);
    }

    @Override
    public GatewayFilter apply(Config config) {
        log.info("Start of filtering request");

        return (exchange, chain) -> {
            try {
                String accessToken = filterService.getAuthToken(exchange.getRequest().getHeaders());
                return filterService.takeUserDetailsFromToken(accessToken)
                        .flatMap(userDetails -> {
                            if (config.isAdminCheck() && !userDetails.getRole().equals(ADMIN_PARAM_VALUE)) {
                                log.warn("No authorities for this");
                                return Mono.error(new ResponseStatusException(HttpStatus.FORBIDDEN, "Access Denied"));
                            }
                            try {
                                return Mono.just(filterService.insertUserDetailsInToResponse(exchange, userDetails));
                            } catch (GatewayServiceException e) {
                                log.warn("Some problems while processing");
                                return Mono.error(new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Some problems while processing"));
                            }
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
