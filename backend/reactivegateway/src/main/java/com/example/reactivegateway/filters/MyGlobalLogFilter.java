package com.example.reactivegateway.filters;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class MyGlobalLogFilter implements GlobalFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getPath().value();
        String method = exchange.getRequest().getMethod().name();

        // Don't log actuator requests to reduce noise
        if (!path.startsWith("/actuator")) {
            System.out.println("=== Gateway Request ===");
            System.out.println("Method: " + method);
            System.out.println("Path: " + path);
            System.out.println("======================");
        }

        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        // Exécuter après le filtre JWT
        return Ordered.HIGHEST_PRECEDENCE + 1;
    }
}