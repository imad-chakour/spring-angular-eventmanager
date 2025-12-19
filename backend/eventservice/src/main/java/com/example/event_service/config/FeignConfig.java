package com.example.event_service.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * Configuration Feign pour transmettre le token JWT aux appels inter-microservices
 */
@Configuration
public class FeignConfig {

    @Bean
    public RequestInterceptor requestInterceptor() {
        return new RequestInterceptor() {
            @Override
            public void apply(RequestTemplate template) {
                // Récupérer la requête HTTP actuelle
                ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
                if (attributes != null) {
                    HttpServletRequest request = attributes.getRequest();
                    // Récupérer le token JWT du header Authorization
                    String authHeader = request.getHeader("Authorization");
                    if (authHeader != null && authHeader.startsWith("Bearer ")) {
                        // Transmettre le token JWT dans les requêtes Feign
                        template.header("Authorization", authHeader);
                        System.out.println("=== FeignConfig: Transmitting JWT token to Feign client ===");
                    } else {
                        System.out.println("=== FeignConfig: No Authorization header found in current request ===");
                    }
                } else {
                    System.out.println("=== FeignConfig: No request attributes found (might be async context) ===");
                }
            }
        };
    }
}

