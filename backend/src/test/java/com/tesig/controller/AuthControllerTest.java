package com.tesig.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tesig.dto.auth.LoginRequest;
import com.tesig.dto.auth.LoginResponse;
import com.tesig.dto.auth.RegisterRequest;
import com.tesig.model.Usuario;
import com.tesig.repository.UsuarioRepository;
import com.tesig.security.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Tests de integración para AuthController.
 */
@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthenticationManager authenticationManager;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @MockBean
    private UsuarioRepository usuarioRepository;

    @MockBean
    private PasswordEncoder passwordEncoder;

    private Usuario usuario;
    private LoginRequest loginRequest;
    private RegisterRequest registerRequest;

    @BeforeEach
    void setUp() {
        usuario = new Usuario();
        usuario.setId(1L);
        usuario.setNombre("Admin");
        usuario.setApellido("Sistema");
        usuario.setEmail("admin@tesig.com");
        usuario.setPassword("$2a$10$encodedPassword");
        usuario.setRol(Usuario.Rol.ADMINISTRADOR);
        usuario.setActivo(true);

        loginRequest = new LoginRequest();
        loginRequest.setEmail("admin@tesig.com");
        loginRequest.setPassword("Admin123!");

        registerRequest = new RegisterRequest();
        registerRequest.setNombre("Nuevo");
        registerRequest.setApellido("Usuario");
        registerRequest.setEmail("nuevo@tesig.com");
        registerRequest.setPassword("Nuevo123!");
        registerRequest.setRol("TECNICO");
    }

    @Test
    @DisplayName("Debe hacer login exitosamente")
    void login_CredencialesValidas_RetornaToken() throws Exception {
        // Arrange
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                usuario.getEmail(), usuario.getPassword());

        when(authenticationManager.authenticate(any())).thenReturn(authentication);
        when(usuarioRepository.findByEmailAndDeletedAtIsNull(anyString()))
                .thenReturn(Optional.of(usuario));
        when(jwtTokenProvider.generateToken(any())).thenReturn("jwt-token-test");

        // Act & Assert
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.token").exists());
    }

    @Test
    @DisplayName("Debe rechazar login con credenciales inválidas")
    void login_CredencialesInvalidas_RetornaError() throws Exception {
        // Arrange
        when(authenticationManager.authenticate(any()))
                .thenThrow(new RuntimeException("Bad credentials"));

        // Act & Assert
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Debe registrar usuario exitosamente")
    void register_DatosValidos_CreaUsuario() throws Exception {
        // Arrange
        when(usuarioRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("$2a$10$encodedPassword");
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuario);

        // Act & Assert
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @DisplayName("Debe rechazar registro con email duplicado")
    void register_EmailDuplicado_RetornaError() throws Exception {
        // Arrange
        when(usuarioRepository.existsByEmail(anyString())).thenReturn(true);

        // Act & Assert
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Debe validar campos requeridos en login")
    void login_CamposFaltantes_RetornaError() throws Exception {
        // Arrange
        LoginRequest invalidRequest = new LoginRequest();
        invalidRequest.setEmail(""); // Email vacío

        // Act & Assert
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Debe validar formato de email en registro")
    void register_EmailInvalido_RetornaError() throws Exception {
        // Arrange
        registerRequest.setEmail("email-invalido");

        // Act & Assert
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isBadRequest());
    }
}
