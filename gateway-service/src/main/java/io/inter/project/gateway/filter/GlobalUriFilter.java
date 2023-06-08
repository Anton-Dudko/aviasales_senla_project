package io.inter.project.gateway.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.filter.ReactiveLoadBalancerClientFilter;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.net.URI;

import static org.springframework.cloud.gateway.support.ServerWebExchangeUtils.GATEWAY_REQUEST_URL_ATTR;
import static org.springframework.cloud.gateway.support.ServerWebExchangeUtils.GATEWAY_ROUTE_ATTR;

@Slf4j
@Component
public class GlobalUriFilter implements GlobalFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        URI incomingUri = exchange.getRequest().getURI();
        log.info("Start of filtering request with GlobalUriFilter -> {}", incomingUri);
        if (isUriEncoded(incomingUri)) {
            Route route = exchange.getAttribute(GATEWAY_ROUTE_ATTR);
            if (route == null) {
                return chain.filter(exchange);
            }
            URI balanceUrl = exchange.getRequiredAttribute(GATEWAY_REQUEST_URL_ATTR);
            URI mergedUri = createUri(incomingUri, balanceUrl);
            log.info("Merged request -> {}", mergedUri);
            exchange.getAttributes().put(GATEWAY_REQUEST_URL_ATTR, mergedUri);
        }

        return chain.filter(exchange);
    }

    private URI createUri(URI incomingUri, URI balanceUrl) {
        String port = balanceUrl.getPort() != -1 ? ":" + balanceUrl.getPort() : "";
        String rawPath = balanceUrl.getRawPath() != null ? balanceUrl.getRawPath() : "";
        String query = incomingUri.getRawQuery() != null ?  "?" + incomingUri.getRawQuery() : "";
        return URI.create(balanceUrl.getScheme() + "://" + balanceUrl.getHost() + port + rawPath + query);
    }

    private static boolean isUriEncoded(URI uri) {
        return (uri.getRawQuery() != null && uri.getRawQuery().contains("%"))
                || (uri.getRawPath() != null && uri.getRawPath().contains("%"));
    }

    @Override
    public int getOrder() {
        return ReactiveLoadBalancerClientFilter.LOAD_BALANCER_CLIENT_FILTER_ORDER + 1;
    }
}