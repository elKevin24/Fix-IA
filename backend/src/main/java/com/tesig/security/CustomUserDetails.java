package com.tesig.security;

import com.tesig.model.Rol;
import com.tesig.model.Usuario;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

/**
 * Implementación personalizada de UserDetails de Spring Security.
 * Envuelve la entidad Usuario y provee la información requerida por Spring Security.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomUserDetails implements UserDetails {

    private Long id;
    private String email;
    private String password;
    private String nombre;
    private String apellido;
    private Rol rol;
    private Boolean activo;

    /**
     * Constructor desde entidad Usuario.
     */
    public static CustomUserDetails fromUsuario(Usuario usuario) {
        return CustomUserDetails.builder()
                .id(usuario.getId())
                .email(usuario.getEmail())
                .password(usuario.getPassword())
                .nombre(usuario.getNombre())
                .apellido(usuario.getApellido())
                .rol(usuario.getRol())
                .activo(usuario.getActivo())
                .build();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Spring Security usa el prefijo ROLE_ por convención
        return List.of(new SimpleGrantedAuthority("ROLE_" + rol.name()));
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        // Spring Security usa username, nosotros usamos email
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return activo;
    }

    public String getNombreCompleto() {
        return nombre + " " + apellido;
    }
}
