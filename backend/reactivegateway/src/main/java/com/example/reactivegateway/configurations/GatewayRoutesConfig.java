package com.example.reactivegateway.configurations;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayRoutesConfig {

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                // Route to User Service with /api/users prefix
                // No rewrite needed - keep /api/users/** as is for JWT filter compatibility
                .route("user-service-api", r -> r
                        .path("/api/users/**")
                        .uri("lb://userservice"))

                // Route to Campaign Service with /api/campaigns prefix
                // No rewrite needed - keep /api/campaigns/** as is to match CampaignController @RequestMapping("/api/campaigns")
                .route("campaign-service-api", r -> r
                        .path("/api/campaigns/**")
                        .uri("lb://campaignservice"))

                // Route to Event Service with /api/events prefix
                // No rewrite needed - keep /api/events/** as is to match EventController @RequestMapping("/api/events")
                .route("event-service-api", r -> r
                        .path("/api/events/**")
                        .uri("lb://eventservice"))

                // Route to Analytics Service with /api/analytics prefix
                // No rewrite needed - keep /api/analytics/** as is
                .route("analytics-service-api", r -> r
                        .path("/api/analytics/**")
                        .uri("lb://analyticsservice"))

                // Route to Notification Service with /api/notifications prefix
                .route("notification-service-api", r -> r
                        .path("/api/notifications/**")
                        .filters(f -> f.rewritePath("/api/notifications/(?<segment>.*)", "/api/${segment}"))
                        .uri("lb://notificationservice"))

                // Gateway test endpoint
                .route("gateway-test", r -> r
                        .path("/gateway/**")
                        .uri("http://httpbin.org"))

                .build();
    }
}