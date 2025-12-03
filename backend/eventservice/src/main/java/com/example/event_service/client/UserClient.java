package com.example.event_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Map;

@FeignClient(name = "userservice", path = "/api/users")
public interface UserClient {

    @GetMapping("/{id}")
    Map<String, Object> getUserById(@PathVariable("id") Long id);
}


