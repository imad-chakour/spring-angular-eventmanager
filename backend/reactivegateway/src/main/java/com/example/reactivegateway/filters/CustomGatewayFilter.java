package com.example.reactivegateway.filters;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class CustomGatewayFilter implements GatewayFilter {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        System.out.println("CustomGatewayFilter: Processing request");

        // Add custom request header
        exchange.getRequest().mutate()
                .header("X-Gateway-Processed", "true")
                .build();

        return chain.filter(exchange);
    }
}