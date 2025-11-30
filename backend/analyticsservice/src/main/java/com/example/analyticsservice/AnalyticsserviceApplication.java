package com.example.analyticsservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class AnalyticsserviceApplication {

	public static void main(String[] args) {
		SpringApplication.run(AnalyticsserviceApplication.class, args);
	}

}
