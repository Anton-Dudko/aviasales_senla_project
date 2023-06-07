package io.inter.project.gateway.configuration;

import io.inter.project.gateway.filter.AuthFilter;
import io.inter.project.gateway.filter.UserIdFilter;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.GatewayFilterSpec;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.*;

@Configuration
@Data
@ConfigurationProperties(prefix = "endpoint")
public class GatewayConfig {

    public static final String URI_ADMIN_VALUE = "admin";
    public static final String URI_USERS_VALUE = "users/${id}";
    public static final String URI_GUEST_VALUE = "guest";
    public static final String CHECK_TRUE_PARAM = "true";
    public static final String CHECK_FALSE_PARAM = "false";
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
                    if (uri.contains(URI_ADMIN_VALUE)) {
                        f.filter(authFilter.apply(new AuthFilter.Config(CHECK_TRUE_PARAM)));
                        rewritePath(f, path, uri);
                    } else if (uri.contains(URI_USERS_VALUE)) {
                        f.filter(userIdFilter.apply(new UserIdFilter.Config()));
                        rewritePath(f, path, uri);
                    } else if (uri.contains(URI_GUEST_VALUE)) {
                        rewritePath(f, path, uri);
                    } else {
                        f.filter(authFilter.apply(new AuthFilter.Config(CHECK_FALSE_PARAM)));
                        rewritePath(f, path, uri);
                    }
                    return f;
                })
                .uri("lb://" + serviceHost)));
    }

    private void rewritePath(GatewayFilterSpec filterSpec, String path, String uri) {
        if (path.contains("/**")) {
            filterSpec.rewritePath(path.replace("**", "(?<id>[^/]+)"), uri);
        } else {
            filterSpec.rewritePath(path, uri);
        }
    }

}

