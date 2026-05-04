package com.fullstack.netflix_backend.utils;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.security.Keys;

import java.security.Key;
import java.util.Date;

public class JwtUtil {

    // 🔐 Secret key (must be long enough)
    private static final String SECRET = "mysecretkeymysecretkeymysecretkey12345";

    private static final Key key = Keys.hmacShaKeyFor(SECRET.getBytes());

    // ✅ Generate JWT Token
    public static String generateToken(String email,String role) {
        return Jwts.builder()
                .setSubject(email) 
                .claim("role", role)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60)) // 1 hour
                .signWith(key)
                .compact();
    }

    // ✅ Extract Email from Token
    public static String extractEmail(String token) {
        return getClaims(token).getSubject();
    }

    public static String extractRole(String token) {
    return getClaims(token).get("role", String.class);
}

    // ✅ Validate Token
    public static boolean isTokenValid(String token) {
        try {
            getClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // 🔍 Internal method
    private static Claims getClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}