package com.example.reactivegateway.configurations;

import com.example.reactivegateway.filters.CustomGatewayFilter;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayRoutesConfig {

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()

                // Route 1 : vers le microservice EMPLOYEES
                .route("employees_route", r -> r
                        .path("/EMPLOYEES/**") // Capture les requêtes commençant par /EMPLOYEES
                        .filters(f -> f
                                .rewritePath("/EMPLOYEES/(?<remaining>.*)", "/${remaining}") // Réécrit le chemin
                                .addRequestHeader("X-Request-Origin", "Gateway") // En-tête personnalisé
                                .filter(new CustomGatewayFilter()) // Filtre personnalisé
                        )
                        .uri("lb://EMPLOYEES")
                )

                // Route 2 : vers le microservice MSPRODUCTS
                .route("msProducts_route", r -> r
                        .path("/MSPRODUCTS/**")
                        .filters(f -> f
                                .rewritePath("/MSPRODUCTS/(?<segment>.*)", "/${segment}")
                        )
                        .uri("lb://MSPRODUCTS")
                )

                // Route 3 : vers le microservice MSCLIENTS
                .route("msClients_route", r -> r
                        .path("/MSCLIENTS/**")
                        .filters(f -> f
                                .rewritePath("/MSCLIENTS/(?<segment>.*)", "/${segment}")
                        )
                        .uri("lb://MSCLIENTS")
                )

                .build();
    }
}
