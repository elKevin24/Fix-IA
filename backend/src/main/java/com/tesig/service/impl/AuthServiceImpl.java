package com.tesig.service.impl;

import com.tesig.dto.auth.LoginRequestDTO;
import com.tesig.dto.auth.LoginResponseDTO;
import com.tesig.dto.auth.RefreshTokenRequestDTO;
import com.tesig.dto.auth.UserInfoDTO;
import com.tesig.exception.ResourceNotFoundException;
import com.tesig.model.RefreshToken;
import com.tesig.model.Usuario;
import com.tesig.repository.RefreshTokenRepository;
import com.tesig.repository.UsuarioRepository;
import com.tesig.security.CustomUserDetails;
import com.tesig.security.IJwtService;
import com.tesig.service.IAuthService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Implementación del servicio de autenticación.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements IAuthService {

    private final AuthenticationManager authenticationManager;
    private final IJwtService jwtService;
    private final UsuarioRepository usuarioRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final HttpServletRequest request; // Para obtener IP y User-Agent

    @Value("${jwt.refresh-expiration}")
    private Long refreshTokenExpiration; // En milisegundos

    @Override
    @Transactional
    public LoginResponseDTO login(LoginRequestDTO loginRequest) {
        log.info("Intento de login para: {}", loginRequest.getEmail());

        try {
            // Autenticar con Spring Security
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getEmail(),
                            loginRequest.getPassword()
                    )
            );

            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

            // Generar tokens
            String accessToken = jwtService.generateAccessToken(userDetails);
            String refreshTokenValue = jwtService.generateRefreshToken(userDetails);

            // Buscar usuario para obtener la entidad completa
            Usuario usuario = usuarioRepository.findByEmail(userDetails.getEmail())
                    .orElseThrow(() -> new ResourceNotFoundException("Usuario", "email", loginRequest.getEmail()));

            // Guardar refresh token en BD
            RefreshToken refreshToken = createRefreshToken(usuario, refreshTokenValue);
            refreshTokenRepository.save(refreshToken);

            // Calcular fecha de expiración del access token
            LocalDateTime expiresAt = LocalDateTime.now().plusSeconds(
                    jwtService.extractClaim(accessToken, claims ->
                        claims.getExpiration().getTime() / 1000 - System.currentTimeMillis() / 1000
                    )
            );

            log.info("Login exitoso para: {} - Rol: {}", usuario.getEmail(), usuario.getRol());

            return LoginResponseDTO.builder()
                    .accessToken(accessToken)
                    .refreshToken(refreshTokenValue)
                    .tokenType("Bearer")
                    .expiresAt(expiresAt)
                    .user(UserInfoDTO.fromUsuario(usuario))
                    .build();

        } catch (BadCredentialsException e) {
            log.warn("Login fallido para: {} - Credenciales inválidas", loginRequest.getEmail());
            throw new BadCredentialsException("Email o contraseña incorrectos");
        }
    }

    @Override
    @Transactional
    public LoginResponseDTO refreshToken(RefreshTokenRequestDTO request) {
        log.info("Solicitud de refresh token");

        String refreshTokenValue = request.getRefreshToken();

        // Buscar refresh token en BD
        RefreshToken refreshToken = refreshTokenRepository.findByToken(refreshTokenValue)
                .orElseThrow(() -> {
                    log.warn("Refresh token no encontrado");
                    return new BadCredentialsException("Refresh token inválido");
                });

        // Validar que el refresh token sea válido
        if (!refreshToken.isValid()) {
            log.warn("Refresh token inválido o expirado");
            refreshToken.invalidate();
            refreshTokenRepository.save(refreshToken);
            throw new BadCredentialsException("Refresh token expirado o inválido");
        }

        // Validar el JWT
        String email = jwtService.extractEmail(refreshTokenValue);
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", "email", email));

        CustomUserDetails userDetails = CustomUserDetails.fromUsuario(usuario);

        if (!jwtService.isTokenValid(refreshTokenValue, userDetails)) {
            log.warn("Refresh token JWT inválido para usuario: {}", email);
            throw new BadCredentialsException("Refresh token inválido");
        }

        // Generar nuevo access token
        String newAccessToken = jwtService.generateAccessToken(userDetails);

        // Opcionalmente, generar nuevo refresh token (rotación de tokens)
        String newRefreshTokenValue = jwtService.generateRefreshToken(userDetails);

        // Invalidar el refresh token anterior
        refreshToken.invalidate();
        refreshTokenRepository.save(refreshToken);

        // Crear y guardar nuevo refresh token
        RefreshToken newRefreshToken = createRefreshToken(usuario, newRefreshTokenValue);
        refreshTokenRepository.save(newRefreshToken);

        LocalDateTime expiresAt = LocalDateTime.now().plusSeconds(
                jwtService.extractClaim(newAccessToken, claims ->
                    claims.getExpiration().getTime() / 1000 - System.currentTimeMillis() / 1000
                )
        );

        log.info("Tokens renovados exitosamente para usuario: {}", email);

        return LoginResponseDTO.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshTokenValue)
                .tokenType("Bearer")
                .expiresAt(expiresAt)
                .user(UserInfoDTO.fromUsuario(usuario))
                .build();
    }

    @Override
    @Transactional
    public void logout(String refreshTokenValue) {
        log.info("Solicitud de logout");

        if (refreshTokenValue == null || refreshTokenValue.isBlank()) {
            log.warn("Logout sin refresh token");
            return;
        }

        refreshTokenRepository.findByToken(refreshTokenValue)
                .ifPresent(refreshToken -> {
                    refreshToken.invalidate();
                    refreshTokenRepository.save(refreshToken);
                    log.info("Refresh token invalidado para usuario: {}", refreshToken.getUsuario().getEmail());
                });

        // Limpiar el contexto de seguridad
        SecurityContextHolder.clearContext();
    }

    @Override
    @Transactional(readOnly = true)
    public UserInfoDTO getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new BadCredentialsException("Usuario no autenticado");
        }

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        return UserInfoDTO.fromUserDetails(userDetails);
    }

    // Métodos privados helpers

    private RefreshToken createRefreshToken(Usuario usuario, String tokenValue) {
        LocalDateTime expiresAt = LocalDateTime.now()
                .plusSeconds(refreshTokenExpiration / 1000);

        return RefreshToken.builder()
                .token(tokenValue)
                .usuario(usuario)
                .fechaExpiracion(expiresAt)
                .activo(true)
                .ipAddress(getClientIP())
                .userAgent(getUserAgent())
                .build();
    }

    private String getClientIP() {
        String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader == null || xfHeader.isEmpty()) {
            return request.getRemoteAddr();
        }
        return xfHeader.split(",")[0];
    }

    private String getUserAgent() {
        String userAgent = request.getHeader("User-Agent");
        if (userAgent != null && userAgent.length() > 500) {
            return userAgent.substring(0, 500);
        }
        return userAgent;
    }
}
