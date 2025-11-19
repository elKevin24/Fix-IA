package com.tesig.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Filtro JWT que intercepta cada request para validar el token.
 * Se ejecuta una vez por cada request.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final IJwtService jwtService;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        // Obtener el header Authorization
        final String authHeader = request.getHeader("Authorization");

        // Si no hay header o no empieza con "Bearer ", continuar sin autenticar
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            // Extraer el token (quitar "Bearer ")
            final String jwt = authHeader.substring(7);
            final String userEmail = jwtService.extractEmail(jwt);

            // Si el email existe y el usuario no está ya autenticado
            if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                // Cargar usuario de la BD
                UserDetails userDetails = userDetailsService.loadUserByUsername(userEmail);

                // Validar el token
                if (jwtService.isTokenValid(jwt, userDetails)) {
                    // Crear authentication token
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );

                    // Agregar detalles del request
                    authToken.setDetails(
                            new WebAuthenticationDetailsSource().buildDetails(request)
                    );

                    // Establecer en el contexto de seguridad
                    SecurityContextHolder.getContext().setAuthentication(authToken);

                    log.debug("Usuario autenticado: {} - Rol: {}",
                            userEmail,
                            userDetails.getAuthorities());
                } else {
                    log.warn("Token inválido para usuario: {}", userEmail);
                }
            }
        } catch (Exception e) {
            log.error("Error procesando JWT: {}", e.getMessage());
            // No lanzar excepción, solo continuar sin autenticar
            // Las rutas protegidas serán bloqueadas por Spring Security
        }

        // Continuar con la cadena de filtros
        filterChain.doFilter(request, response);
    }
}
