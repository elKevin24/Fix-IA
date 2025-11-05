package com.tesig.mapper;

import com.tesig.dto.ClienteBasicoDTO;
import com.tesig.model.Cliente;
import org.mapstruct.*;

/**
 * Mapper para conversiones entre entidad Cliente y DTOs.
 */
@Mapper(
    componentModel = "spring",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
    unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface ClienteMapper {

    /**
     * Convierte entidad Cliente a DTO básico.
     * Solo incluye información pública básica.
     */
    ClienteBasicoDTO toBasicoDTO(Cliente cliente);

    /**
     * Método default para calcular el nombre completo.
     * MapStruct lo usará automáticamente si lo necesita.
     */
    @AfterMapping
    default void setNombreCompleto(@MappingTarget ClienteBasicoDTO dto, Cliente cliente) {
        dto.setNombreCompleto(cliente.getNombreCompleto());
    }
}
