package com.tesig.mapper;

import com.tesig.dto.ClienteBasicoDTO;
import com.tesig.dto.cliente.ClienteCreateDTO;
import com.tesig.dto.cliente.ClienteDTO;
import com.tesig.dto.cliente.ClienteUpdateDTO;
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
     * Convierte entidad Cliente a DTO completo.
     */
    ClienteDTO toDTO(Cliente cliente);

    /**
     * Convierte entidad Cliente a DTO básico.
     * Solo incluye información pública básica.
     */
    ClienteBasicoDTO toBasicoDTO(Cliente cliente);

    /**
     * Convierte DTO de creación a entidad Cliente.
     */
    Cliente toEntity(ClienteCreateDTO dto);

    /**
     * Actualiza una entidad Cliente existente con los datos del DTO.
     * Solo actualiza los campos no nulos.
     */
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDTO(ClienteUpdateDTO dto, @MappingTarget Cliente cliente);

    /**
     * Método ejecutado después del mapeo para calcular campos derivados.
     */
    @AfterMapping
    default void afterMapping(@MappingTarget ClienteDTO dto, Cliente cliente) {
        dto.setNombreCompleto(cliente.getNombreCompleto());
        if (cliente.getTickets() != null) {
            dto.setTotalTickets(cliente.getTickets().size());
        } else {
            dto.setTotalTickets(0);
        }
    }

    @AfterMapping
    default void afterMappingBasico(@MappingTarget ClienteBasicoDTO dto, Cliente cliente) {
        dto.setNombreCompleto(cliente.getNombreCompleto());
    }
}
