package com.example.msproducts;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class MsproductsApplication {

	public static void main(String[] args) {
		SpringApplication.run(MsproductsApplication.class, args);
	}

}
