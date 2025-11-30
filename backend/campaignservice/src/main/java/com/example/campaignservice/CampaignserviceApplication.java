package com.example.campaignservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class CampaignserviceApplication {

	public static void main(String[] args) {
		SpringApplication.run(CampaignserviceApplication.class, args);
	}

}
