package io.inter.project.gateway.configuration;

import io.inter.project.gateway.filter.AuthFilter;
import io.inter.project.gateway.filter.UserIdFilter;
import lombok.Data;
import org.apache.commons.collections4.MapUtils;
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
    public static final String URI_EXTERNAL_VALUE = "external";

    private final List<String> exclusionList = new ArrayList<>();

    private final List<String> excludedEndpointList = new ArrayList<>();

    private final Map<String, Map<String, String>> mappings = new LinkedHashMap<>();

    @Bean
    public RouteLocator createRoutes(RouteLocatorBuilder builder,
                                     AuthFilter authFilter,
                                     UserIdFilter userIdFilter) {
        RouteLocatorBuilder.Builder routes = builder.routes();
        if (!MapUtils.isEmpty(mappings)) {
            mappings.forEach((host, routesMap) -> addRoutes(authFilter, userIdFilter, routes, routesMap, host));
        }
        return routes.build();
    }

    private void addRoutes(AuthFilter authFilter,
                           UserIdFilter userIdFilter,
                           RouteLocatorBuilder.Builder routes,
                           Map<String, String> serviceMap,
                           String serviceHost) {
        if (!MapUtils.isEmpty(serviceMap)) {
            serviceMap.forEach((path, uri) -> routes.route(UUID.randomUUID().toString(), r -> r.path(path)
                    .filters(filter -> {
                        if (uri.contains(URI_ADMIN_VALUE) || uri.contains(URI_EXTERNAL_VALUE) ) {
                            filter.filter(authFilter.apply(new AuthFilter.Config(CHECK_TRUE_PARAM)));
                            rewritePath(filter, path, uri);
                        } else if (uri.contains(URI_USERS_VALUE)) {
                            filter.filter(userIdFilter.apply(new UserIdFilter.Config()));
                            rewritePath(filter, path, uri);
                        } else if (uri.contains(URI_GUEST_VALUE)) {
                            rewritePath(filter, path, uri);
                        } else {
                            filter.filter(authFilter.apply(new AuthFilter.Config(CHECK_FALSE_PARAM)));
                            rewritePath(filter, path, uri);
                        }
                        return filter;
                    })
                    .uri(String.format("lb://%s",serviceHost))));
        }
    }

    private void rewritePath(GatewayFilterSpec filterSpec,
                             String path,
                             String uri) {
        if (path.contains("/**")) {
            filterSpec.rewritePath(path.replace("**", "(?<id>[^/]+)"), uri);
        } else {
            filterSpec.rewritePath(path, uri);
        }
    }

}

