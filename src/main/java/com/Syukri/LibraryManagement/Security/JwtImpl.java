package com.Syukri.LibraryManagement.Security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

@Component
public class JwtImpl {

    static final long EXPIRATION_TIME = 864000000;
    static final String
            PREFIX = "Bearer ";

    // The signing key is config, not code (12-factor III): it is injected from
    // the environment so tokens stay valid across restarts and instances,
    // instead of a random key being generated per process.
    private final Key key;

    public JwtImpl(@Value("${jwt.secret}") String secret) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    public String generateToken(String username) {
        String token = Jwts.builder()
                .setSubject(username)
                .setExpiration(new Date(System.currentTimeMillis()
                        + EXPIRATION_TIME))
                .signWith(key)
                .compact();

        return token;
    }

    public String getAuthUser(HttpServletRequest
                                      request) {
        String token = request.getHeader
                (HttpHeaders.AUTHORIZATION);

        if (token != null) {
            String user = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token.replace(PREFIX, ""))
                    .getBody()
                    .getSubject();

            if (user != null)
                return user;
        }
        return null;
    }
}
