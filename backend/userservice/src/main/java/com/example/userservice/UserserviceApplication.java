package com.example.userservice;

import io.jsonwebtoken.security.Keys;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.crypto.SecretKey;
import java.util.Base64;

@SpringBootApplication
@EnableFeignClients
public class UserserviceApplication {

	public static void main(String[] args) {

        SpringApplication.run(UserserviceApplication.class, args);
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        // Hash for "admin"
        String adminHash = encoder.encode("admin");
        System.out.println("BCrypt hash for 'admin': " + adminHash);

        // Verify it works
        boolean matches = encoder.matches("admin", adminHash);
        System.out.println("Password matches: " + matches);

        SecretKey key = Keys.secretKeyFor(io.jsonwebtoken.SignatureAlgorithm.HS256);

        // Encode to Base64
        String base64Key = Base64.getEncoder().encodeToString(key.getEncoded());

        System.out.println("Generated JWT Secret (256-bit):");
        System.out.println(base64Key);
        System.out.println("\nAdd this to your application.properties:");
        System.out.println("security.jwt.secret=" + base64Key);
    }

}
