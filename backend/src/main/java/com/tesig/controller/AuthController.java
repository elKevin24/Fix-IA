package com.tesig.controller;

import com.tesig.dto.ApiResponse;
import com.tesig.dto.auth.LoginRequestDTO;
import com.tesig.dto.auth.LoginResponseDTO;
import com.tesig.dto.auth.RefreshTokenRequestDTO;
import com.tesig.dto.auth.UserInfoDTO;
import com.tesig.service.IAuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador de autenticación.
 * Maneja login, refresh de tokens y logout.
 */
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Autenticación", description = "Endpoints para autenticación y gestión de sesiones")
public class AuthController {

    private final IAuthService authService;

    @Operation(
        summary = "Login de usuario",
        description = "Autentica un usuario con email y contraseña. Retorna access token y refresh token."
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Login exitoso",
            content = @Content(schema = @Schema(implementation = LoginResponseDTO.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "401",
            description = "Credenciales inválidas"
        )
    })
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponseDTO>> login(
            @Valid @RequestBody LoginRequestDTO request
    ) {
        log.info("POST /api/auth/login - Login attempt for: {}", request.getEmail());

        LoginResponseDTO response = authService.login(request);

        return ResponseEntity.ok(
                ApiResponse.success("Login exitoso", response)
        );
    }

    @Operation(
        summary = "Renovar access token",
        description = "Genera un nuevo access token usando el refresh token. " +
                     "También genera un nuevo refresh token (rotación de tokens)."
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Token renovado exitosamente",
            content = @Content(schema = @Schema(implementation = LoginResponseDTO.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "401",
            description = "Refresh token inválido o expirado"
        )
    })
    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<LoginResponseDTO>> refreshToken(
            @Valid @RequestBody RefreshTokenRequestDTO request
    ) {
        log.info("POST /api/auth/refresh - Refresh token request");

        LoginResponseDTO response = authService.refreshToken(request);

        return ResponseEntity.ok(
                ApiResponse.success("Token renovado exitosamente", response)
        );
    }

    @Operation(
        summary = "Cerrar sesión",
        description = "Invalida el refresh token del usuario. El cliente debe eliminar los tokens almacenados."
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Logout exitoso"
        )
    })
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(
            @Valid @RequestBody RefreshTokenRequestDTO request
    ) {
        log.info("POST /api/auth/logout - Logout request");

        authService.logout(request.getRefreshToken());

        return ResponseEntity.ok(
                ApiResponse.success("Sesión cerrada exitosamente", null)
        );
    }

    @Operation(
        summary = "Obtener usuario autenticado",
        description = "Retorna la información del usuario actualmente autenticado",
        security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Usuario autenticado",
            content = @Content(schema = @Schema(implementation = UserInfoDTO.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "401",
            description = "No autenticado"
        )
    })
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserInfoDTO>> getCurrentUser() {
        log.info("GET /api/auth/me - Get current user");

        UserInfoDTO user = authService.getCurrentUser();

        return ResponseEntity.ok(
                ApiResponse.success("Usuario autenticado", user)
        );
    }
}
