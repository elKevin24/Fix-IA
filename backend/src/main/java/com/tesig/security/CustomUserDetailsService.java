package com.tesig.security;

import com.tesig.model.Usuario;
import com.tesig.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Servicio para cargar usuarios desde la base de datos.
 * Implementa UserDetailsService de Spring Security.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CustomUserDetailsService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        log.debug("Cargando usuario por email: {}", email);

        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.error("Usuario no encontrado: {}", email);
                    return new UsernameNotFoundException("Usuario no encontrado con email: " + email);
                });

        // Validar que el usuario no esté eliminado
        if (usuario.isDeleted()) {
            log.error("Intento de acceso con usuario eliminado: {}", email);
            throw new UsernameNotFoundException("Usuario no encontrado con email: " + email);
        }

        // Validar que el usuario esté activo
        if (!usuario.getActivo()) {
            log.warn("Intento de acceso con usuario inactivo: {}", email);
            throw new UsernameNotFoundException("Usuario inactivo: " + email);
        }

        log.debug("Usuario cargado exitosamente: {} - Rol: {}", email, usuario.getRol());

        return CustomUserDetails.fromUsuario(usuario);
    }
}
