package io.inter.project.gateway.configuration;

import io.inter.project.gateway.filter.AuthFilter;
import io.inter.project.gateway.filter.UserIdFilter;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.GatewayFilterSpec;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.*;

@Configuration
@Getter
@Setter
@Slf4j
@ConfigurationProperties(prefix = "endpoint")
public class GatewayConfig {

    private final List<String> exclusionList = new ArrayList<>();

    private final Map<String, Map<String, String>> mappings = new LinkedHashMap<>();

    @Bean
    public RouteLocator createRoutes(RouteLocatorBuilder builder, AuthFilter authFilter, UserIdFilter userIdFilter) {
        RouteLocatorBuilder.Builder routes = builder.routes();
        mappings.forEach((host, routesMap) -> addRoutes(authFilter, userIdFilter, routes, routesMap, host));
        return routes.build();
    }

    private void addRoutes(AuthFilter authFilter, UserIdFilter userIdFilter, RouteLocatorBuilder.Builder routes, Map<String, String> serviceMap, String serviceHost) {
        serviceMap.forEach((path, uri) -> routes.route(UUID.randomUUID().toString(), r -> r.path(path)
                .filters(f -> {
                    if (uri.contains("admin")) {
                        f.filter(authFilter.apply(new AuthFilter.Config("true")));
                        handlePathRewriting(f, path, uri);
                    } else if (uri.contains("users/${id}")) {
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

}

