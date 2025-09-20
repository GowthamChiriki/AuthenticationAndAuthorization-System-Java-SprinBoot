package com.authsystem.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;

import java.security.Key;
import java.util.Date;

public class JwtUtil {
    // NOTE: In production keep secret key safe and long. Here for demo we hardcode.
    private static final Key SECRET_KEY = Keys.hmacShaKeyFor("VerySecretKeyChangeThisToLongRandomString1234567890".getBytes());
    private static final long EXPIRATION_MS = 1000L * 60 * 60 * 24; // 24 hours

    public static String generateToken(Long userId, String username, String role) {
        Date now = new Date();
        Date exp = new Date(now.getTime() + EXPIRATION_MS);

        return Jwts.builder()
                .setSubject(String.valueOf(userId))
                .claim("username", username)
                .claim("role", role)
                .setIssuedAt(now)
                .setExpiration(exp)
                .signWith(SECRET_KEY, SignatureAlgorithm.HS256)
                .compact();
    }

    public static Jws<Claims> validateToken(String token) throws JwtException {
        return Jwts.parserBuilder()
                .setSigningKey(SECRET_KEY)
                .build()
                .parseClaimsJws(token);
    }

    public static Long getUserIdFromToken(String token) {
        Claims claims = validateToken(token).getBody();
        return Long.parseLong(claims.getSubject());
    }

    public static String getRoleFromToken(String token) {
        Claims claims = validateToken(token).getBody();
        return claims.get("role", String.class);
    }

    public static String getUsernameFromToken(String token) {
        Claims claims = validateToken(token).getBody();
        return claims.get("username", String.class);
    }
}
