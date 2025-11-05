package com.tesig.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Implementación del servicio JWT usando jjwt library.
 */
@Service
@Slf4j
public class JwtServiceImpl implements IJwtService {

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.expiration}")
    private Long accessTokenExpiration; // En milisegundos

    @Value("${jwt.refresh-expiration}")
    private Long refreshTokenExpiration; // En milisegundos

    @Override
    public String generateAccessToken(UserDetails userDetails) {
        Map<String, Object> extraClaims = buildExtraClaims(userDetails);
        return generateAccessToken(extraClaims, userDetails);
    }

    @Override
    public String generateAccessToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        return buildToken(extraClaims, userDetails, accessTokenExpiration);
    }

    @Override
    public String generateRefreshToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        // Refresh token solo tiene el subject (email), sin claims adicionales
        return buildToken(claims, userDetails, refreshTokenExpiration);
    }

    @Override
    public String extractEmail(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    @Override
    public Long extractUserId(String token) {
        return extractClaim(token, claims -> claims.get("userId", Long.class));
    }

    @Override
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    @Override
    public boolean isTokenValid(String token, UserDetails userDetails) {
        try {
            final String email = extractEmail(token);
            boolean emailMatches = email.equals(userDetails.getUsername());
            boolean notExpired = !isTokenExpired(token);

            return emailMatches && notExpired;
        } catch (Exception e) {
            log.error("Error validando token: {}", e.getMessage());
            return false;
        }
    }

    @Override
    public boolean isTokenExpired(String token) {
        try {
            return extractExpiration(token).before(new Date());
        } catch (Exception e) {
            log.error("Error verificando expiración del token: {}", e.getMessage());
            return true;
        }
    }

    // Métodos privados helpers

    private String buildToken(
            Map<String, Object> extraClaims,
            UserDetails userDetails,
            Long expiration
    ) {
        long nowMillis = System.currentTimeMillis();
        Date now = new Date(nowMillis);
        Date expiryDate = new Date(nowMillis + expiration);

        return Jwts.builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername()) // Email
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    private Map<String, Object> buildExtraClaims(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();

        // Agregar roles/authorities
        claims.put("authorities", userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList()));

        // Si es nuestro CustomUserDetails, agregar más info
        if (userDetails instanceof CustomUserDetails) {
            CustomUserDetails customUser = (CustomUserDetails) userDetails;
            claims.put("userId", customUser.getId());
            claims.put("nombre", customUser.getNombreCompleto());
            claims.put("rol", customUser.getRol());
        }

        return claims;
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
