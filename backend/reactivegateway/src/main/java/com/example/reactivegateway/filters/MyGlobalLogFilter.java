package com.example.reactivegateway.filters;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class MyGlobalLogFilter implements GlobalFilter, Ordered {
    /// Spring Cloud Gateway utilise WebFlux (réactif), donc tout est non bloquant.
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String url = exchange.getRequest().getURI().toString();
        System.out.println("MyGlobalLogFilter : Requête interceptée ! URL : {}" + url);

        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return -1;
    }
}