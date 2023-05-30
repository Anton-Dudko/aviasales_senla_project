package io.inter.project.gateway.configuration;

import io.inter.project.gateway.filter.AuthFilter;
import io.inter.project.gateway.filter.UserIdFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.GatewayFilterSpec;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.*;

@Configuration
@ConfigurationProperties(prefix = "endpoint")
public class GatewayConfig {

    private Map<String, String> userService = new LinkedHashMap<>();

    private Map<String, String> financeService = new LinkedHashMap<>();

    private Map<String, String> tripService = new LinkedHashMap<>();

    private Map<String, String> notificationService = new LinkedHashMap<>();

    private Map<String, String> ticketsService = new LinkedHashMap<>();



    @Value("${cloud.finance-service-host}")
    private String financeServiceHost;

    @Value("${cloud.userservice-host}")
    private String userServiceHost;

    @Value("${cloud.ticket-service-host}")
    private String ticketServiceHost;

    @Value("${cloud.trip-service-host}")
    private String tripServiceHost;

    @Value("${cloud.notification-service-host}")
    private String notificationServiceHost;

    @Bean
    public RouteLocator routes(RouteLocatorBuilder builder, AuthFilter authFilter, UserIdFilter userIdFilter) {
        RouteLocatorBuilder.Builder routes = builder.routes();

        addRoutes(authFilter, userIdFilter, routes, userService, userServiceHost);

        addRoutes(authFilter, userIdFilter, routes, financeService, financeServiceHost);

        addRoutes(authFilter, userIdFilter, routes, tripService, tripServiceHost);

        addRoutes(authFilter, userIdFilter, routes, notificationService, notificationServiceHost);

        addRoutes(authFilter, userIdFilter, routes, ticketsService, ticketServiceHost);

        return routes.build();
    }

    private void addRoutes(AuthFilter authFilter, UserIdFilter userIdFilter, RouteLocatorBuilder.Builder routes, Map<String, String> serviceMap, String serviceHost) {
        serviceMap.forEach((path, uri) -> routes.route(UUID.randomUUID().toString(), r -> r.path(path)
                .filters(f -> {
                    if (uri.contains("admin")) {
                        f.filter(authFilter.apply(new AuthFilter.Config("true")));
                        handlePathRewriting(f, path, uri);
                    } else if (uri.contains("users/{id}")) {
                        f.filter(userIdFilter.apply(new UserIdFilter.Config()));
                        handlePathRewriting(f, path, uri);
                    } else if (uri.contains("guest")) {
                        handlePathRewriting(f, path, uri);
                    } else {
                        f.filter(authFilter.apply(new AuthFilter.Config("false")));
                        handlePathRewriting(f, path, uri);
                    }
                    return f;
                })
                .uri("lb://" + serviceHost)));
    }

    private void handlePathRewriting(GatewayFilterSpec f, String path, String uri) {
        if (path.contains("/**")) {
            f.rewritePath(path.replace("**", "(?<id>[^/]+)"), uri);
        } else {
            f.rewritePath(path, uri);
        }
    }

    public void setUserService(Map<String, String> userService) {
        this.userService = userService;
    }

    public void setFinanceService(Map<String, String> financeService) {
        this.financeService = financeService;
    }

    public void setTripService(Map<String, String> tripService) {
        this.tripService = tripService;
    }

    public void setNotificationService(Map<String, String> notificationService) {
        this.notificationService = notificationService;
    }

    public void setTicketsService(Map<String, String> ticketsService) {
        this.ticketsService = ticketsService;
    }
}

