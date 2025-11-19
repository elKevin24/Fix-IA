package com.tesig.dto.auth;

import com.tesig.model.Rol;
import com.tesig.model.Usuario;
import com.tesig.security.CustomUserDetails;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO con información del usuario autenticado.
 * NO incluye información sensible como password.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserInfoDTO {

    private Long id;
    private String email;
    private String nombre;
    private String apellido;
    private String nombreCompleto;
    private Rol rol;
    private String rolNombre;

    public static UserInfoDTO fromUsuario(Usuario usuario) {
        return UserInfoDTO.builder()
                .id(usuario.getId())
                .email(usuario.getEmail())
                .nombre(usuario.getNombre())
                .apellido(usuario.getApellido())
                .nombreCompleto(usuario.getNombreCompleto())
                .rol(usuario.getRol())
                .rolNombre(usuario.getRol().getNombre())
                .build();
    }

    public static UserInfoDTO fromUserDetails(CustomUserDetails userDetails) {
        return UserInfoDTO.builder()
                .id(userDetails.getId())
                .email(userDetails.getEmail())
                .nombre(userDetails.getNombre())
                .apellido(userDetails.getApellido())
                .nombreCompleto(userDetails.getNombreCompleto())
                .rol(userDetails.getRol())
                .rolNombre(userDetails.getRol().getNombre())
                .build();
    }
}
