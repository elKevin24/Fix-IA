package com.tesig.security;

import io.jsonwebtoken.Claims;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Map;
import java.util.function.Function;

/**
 * Servicio para gestión de JWT (JSON Web Tokens).
 *
 * Responsabilidades:
 * - Generar access tokens y refresh tokens
 * - Validar tokens
 * - Extraer información (claims) de los tokens
 */
public interface IJwtService {

    /**
     * Genera un access token para el usuario.
     *
     * @param userDetails Detalles del usuario
     * @return Access token JWT
     */
    String generateAccessToken(UserDetails userDetails);

    /**
     * Genera un access token con claims adicionales.
     *
     * @param extraClaims Claims personalizados
     * @param userDetails Detalles del usuario
     * @return Access token JWT
     */
    String generateAccessToken(Map<String, Object> extraClaims, UserDetails userDetails);

    /**
     * Genera un refresh token para el usuario.
     *
     * @param userDetails Detalles del usuario
     * @return Refresh token JWT
     */
    String generateRefreshToken(UserDetails userDetails);

    /**
     * Extrae el email (subject) del token.
     *
     * @param token JWT
     * @return Email del usuario
     */
    String extractEmail(String token);

    /**
     * Extrae el ID del usuario del token.
     *
     * @param token JWT
     * @return ID del usuario
     */
    Long extractUserId(String token);

    /**
     * Extrae un claim específico del token.
     *
     * @param token JWT
     * @param claimsResolver Función para extraer el claim
     * @param <T> Tipo del claim
     * @return Valor del claim
     */
    <T> T extractClaim(String token, Function<Claims, T> claimsResolver);

    /**
     * Valida si el token es válido para el usuario.
     *
     * @param token JWT
     * @param userDetails Detalles del usuario
     * @return true si es válido, false en caso contrario
     */
    boolean isTokenValid(String token, UserDetails userDetails);

    /**
     * Verifica si el token ha expirado.
     *
     * @param token JWT
     * @return true si expiró, false si aún es válido
     */
    boolean isTokenExpired(String token);
}
