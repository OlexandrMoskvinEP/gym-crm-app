package com.gym.crm.app.security.jwt;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gym.crm.app.exception.UnacceptableOperationException;
import com.gym.crm.app.security.model.AuthenticatedUser;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
@RequiredArgsConstructor
public class JwtTokenProvider {
    private static final long EXPIRATION_MILLIS = 60 * 60 * 1000L;

    private final ObjectMapper objectMapper;

    @Value("${security.jwt.secret}")
    private String secret;
    private SecretKey secretKey;

    @PostConstruct
    public void init() {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    public String generateToken(AuthenticatedUser user) {
        try {
            String userJson = objectMapper.writeValueAsString(user);

            return Jwts.builder()
                    .setSubject(user.getUsername())
                    .claim("user", userJson)
                    .setIssuedAt(new Date())
                    .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_MILLIS))
                    .signWith(secretKey)
                    .compact();
        } catch (JsonProcessingException e) {
            throw new UnacceptableOperationException("Error serializing user to JWT");
        }
    }

    public AuthenticatedUser parseToken(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            String userJson = claims.get("user", String.class);

            return objectMapper.readValue(userJson, AuthenticatedUser.class);
        } catch (Exception e) {
            throw new UnacceptableOperationException("Invalid or expired JWT token");
        }
    }
}
