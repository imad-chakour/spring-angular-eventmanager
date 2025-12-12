package com.example.reactivegateway.filters;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * Filtre pour gérer les erreurs du Gateway
 * Convertit les erreurs 403/503 en messages plus clairs
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 10)
public class GatewayErrorFilter implements GlobalFilter {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        return chain.filter(exchange)
                .onErrorResume(Exception.class, ex -> {
                    ServerHttpResponse response = exchange.getResponse();
                    
                    System.err.println("=== Gateway Error ===");
                    System.err.println("Path: " + exchange.getRequest().getURI().getPath());
                    System.err.println("Error: " + ex.getClass().getSimpleName());
                    System.err.println("Message: " + ex.getMessage());
                    ex.printStackTrace();
                    
                    // Si c'est une erreur de service non trouvé
                    if (ex.getMessage() != null && 
                        (ex.getMessage().contains("503") || 
                         ex.getMessage().contains("Service Unavailable") ||
                         ex.getMessage().contains("LoadBalancer") ||
                         ex.getMessage().contains("No instances"))) {
                        
                        response.setStatusCode(HttpStatus.SERVICE_UNAVAILABLE);
                        response.getHeaders().add("Content-Type", "application/json");
                        
                        String errorBody = "{\"error\":\"Service Unavailable\",\"message\":\"The requested service is not available. Please check if the service is registered in Eureka.\"}";
                        return response.writeWith(
                                Mono.just(response.bufferFactory().wrap(errorBody.getBytes()))
                        );
                    }
                    
                    // Pour les autres erreurs, laisser Spring Gateway les gérer
                    return Mono.error(ex);
                });
    }
}
