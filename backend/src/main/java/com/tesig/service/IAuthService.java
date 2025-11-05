package com.tesig.service;

import com.tesig.dto.auth.LoginRequestDTO;
import com.tesig.dto.auth.LoginResponseDTO;
import com.tesig.dto.auth.RefreshTokenRequestDTO;
import com.tesig.dto.auth.UserInfoDTO;

/**
 * Servicio de autenticación.
 *
 * Responsabilidades:
 * - Login de usuarios
 * - Renovación de tokens
 * - Logout (invalidación de tokens)
 */
public interface IAuthService {

    /**
     * Autentica un usuario con email y contraseña.
     *
     * @param request Datos de login
     * @return Información del usuario y tokens JWT
     */
    LoginResponseDTO login(LoginRequestDTO request);

    /**
     * Renueva el access token usando el refresh token.
     *
     * @param request Refresh token
     * @return Nuevo access token
     */
    LoginResponseDTO refreshToken(RefreshTokenRequestDTO request);

    /**
     * Cierra la sesión del usuario e invalida sus tokens.
     *
     * @param refreshToken Refresh token a invalidar
     */
    void logout(String refreshToken);

    /**
     * Obtiene la información del usuario autenticado actualmente.
     *
     * @return Información del usuario
     */
    UserInfoDTO getCurrentUser();
}
