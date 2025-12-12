package com.example.reactivegateway.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

/**
 * Validateur JWT pour le Gateway
 * Utilise la même clé secrète que le userservice
 */
@Component
public class JwtTokenValidator {

    @Value("${security.jwt.secret:ZmFrZV9zZWNyZXRfZm9yX2Rldl9vbmx5}")
    private String secret;

    private Key getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * Valide le token JWT
     */
    public boolean validateToken(String token) {
        try {
            Claims claims = getAllClaims(token);
            return claims.getExpiration().after(new Date());
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Extrait le nom d'utilisateur du token
     */
    public String getUsername(String token) {
        return getAllClaims(token).getSubject();
    }

    /**
     * Extrait les rôles du token
     */
    public String getRoles(String token) {
        return getAllClaims(token).get("roles", String.class);
    }

    /**
     * Extrait toutes les claims du token
     */
    private Claims getAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
