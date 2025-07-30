package com.LibreriaCreativa.LibreriaCreativa.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private long expiration;

    private SecretKey getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateToken(String username, boolean isAdmin) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiration);

        String role = isAdmin ? "ROLE_ADMIN" : "ROLE_USER";

        return Jwts.builder()
                .subject(username)
                .claim("roles", role)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(getSigningKey())
                .compact();
    }

    public String getUsernameFromToken(String token) {
        return parseClaims(token).getPayload().getSubject();
    }

    public boolean isTokenValid(String token) {
        try {
            Jws<Claims> claims = parseClaims(token);
            return !claims.getPayload().getExpiration().before(new Date());
        } catch (Exception e) {
            return false;
        }
    }

    private Jws<Claims> parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token);
    }

    public boolean validateToken(String token, String username) {
        try {
            final String tokenUsername = getUsernameFromToken(token);
            return (tokenUsername.equals(username) && !isTokenExpired(token));
        } catch (Exception e) {
            return false;
        }
    }

    private boolean isTokenExpired(String token) {
        final Date expirationDate = parseClaims(token).getPayload().getExpiration();
        return expirationDate.before(new Date());
    }

    public String getRoleFromToken(String token) {
        return parseClaims(token).getPayload().get("roles", String.class);
    }

}
