package com.example.reactivegateway.filters;

import com.example.reactivegateway.security.JwtTokenValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * Filtre global pour valider les tokens JWT dans le Gateway
 * Exclut les endpoints publics (login, register, actuator)
 */
@Component
public class JwtAuthenticationFilter implements GlobalFilter, Ordered {

    @Autowired
    private JwtTokenValidator jwtTokenValidator;

    // Endpoints publics qui ne nécessitent pas d'authentification
    private static final List<String> PUBLIC_ENDPOINTS = List.of(
            "/api/users/login",
            "/api/users/register",
            "/actuator"
    );

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();
        String method = request.getMethod() != null ? request.getMethod().name() : "UNKNOWN";

        System.out.println("=== JWT Filter ===");
        System.out.println("Path: " + path);
        System.out.println("Method: " + method);

        // Laisser passer les requêtes OPTIONS (preflight CORS)
        if ("OPTIONS".equals(method)) {
            System.out.println("Allowing OPTIONS request");
            return chain.filter(exchange);
        }

        // Vérifier si l'endpoint est public
        if (isPublicEndpoint(path)) {
            System.out.println("Allowing public endpoint: " + path);
            return chain.filter(exchange);
        }

        System.out.println("Endpoint requires authentication: " + path);

        // Extraire le token du header Authorization
        String authHeader = request.getHeaders().getFirst("Authorization");
        String token = null;

        if (StringUtils.hasText(authHeader) && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);
        }

        // Si pas de token, rejeter la requête
        if (token == null || !jwtTokenValidator.validateToken(token)) {
            ServerHttpResponse response = exchange.getResponse();
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            response.getHeaders().add("Content-Type", "application/json");
            
            String errorBody = "{\"error\":\"Unauthorized\",\"message\":\"Invalid or missing JWT token\"}";
            return response.writeWith(
                    Mono.just(response.bufferFactory().wrap(errorBody.getBytes()))
            );
        }

        // Ajouter les informations utilisateur aux headers pour les microservices
        String username = jwtTokenValidator.getUsername(token);
        String roles = jwtTokenValidator.getRoles(token);

        ServerHttpRequest modifiedRequest = request.mutate()
                .header("X-User-Email", username)
                .header("X-User-Roles", roles != null ? roles : "")
                .build();

        return chain.filter(exchange.mutate().request(modifiedRequest).build());
    }

    /**
     * Vérifie si l'endpoint est public (ne nécessite pas d'authentification)
     */
    private boolean isPublicEndpoint(String path) {
        return PUBLIC_ENDPOINTS.stream()
                .anyMatch(path::startsWith);
    }

    @Override
    public int getOrder() {
        // Exécuter après le filtre CORS mais avant les autres filtres
        return -50;
    }
}
