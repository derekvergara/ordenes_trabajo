package com.ordenes_back.security;
import io.jsonwebtoken.*;
import org.springframework.stereotype.Component;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtUtil {
    // encriptacion asimetrica
    private final PrivateKey privateKey;
    private final PublicKey publicKey;
    private final long expiration = 1000 * 60 * 60 * 8; // Aumentado a 8 horas para jornada laboral

    public JwtUtil() {
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(2048);
            KeyPair keyPair = keyPairGenerator.generateKeyPair();
            this.privateKey = keyPair.getPrivate();
            this.publicKey = keyPair.getPublic();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error al generar llaves RSA asimetricas", e);
        }
    }

    public String generateToken(String username, String rol) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("rol", rol);
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                // firmamos el token
                .signWith(privateKey, SignatureAlgorithm.RS256)
                .compact();
    }

    public String extractUsername(String token) {
        // validamos el token publico
        return Jwts.parserBuilder().setSigningKey(publicKey).build()
                .parseClaimsJws(token).getBody().getSubject();
    }

    public String extractRol(String token) {
        // verificamos el token publico
        return (String) Jwts.parserBuilder().setSigningKey(publicKey).build()
                .parseClaimsJws(token).getBody().get("rol");
    }

    public boolean validateToken(String token) {
        try {
            // verificamos algun error si el token es correcto
            Jwts.parserBuilder().setSigningKey(publicKey).build().parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }
}
