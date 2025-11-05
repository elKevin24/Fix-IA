package com.tesig.mapper;

import com.tesig.dto.auth.UserInfoDTO;
import com.tesig.model.Usuario;
import org.mapstruct.*;

/**
 * Mapper para conversiones de Usuario a DTOs.
 * Aplicando Single Responsibility Principle.
 */
@Mapper(
    componentModel = "spring",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
    unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface UsuarioMapper {

    /**
     * Convierte Usuario a UserInfoDTO.
     */
    @Mapping(source = "rol", target = "rol")
    UserInfoDTO toUserInfoDTO(Usuario usuario);

    @AfterMapping
    default void afterMapping(@MappingTarget UserInfoDTO dto, Usuario usuario) {
        if (usuario != null) {
            dto.setNombreCompleto(usuario.getNombreCompleto());
            dto.setRolNombre(usuario.getRol().getNombre());
        }
    }
}
