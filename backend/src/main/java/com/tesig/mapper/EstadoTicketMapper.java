package com.tesig.mapper;

import com.tesig.dto.EstadoTicketDTO;
import com.tesig.model.EstadoTicket;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

/**
 * Mapper para conversiones del enum EstadoTicket.
 */
@Mapper(
    componentModel = "spring",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
    unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface EstadoTicketMapper {

    /**
     * Convierte enum EstadoTicket a DTO.
     */
    default EstadoTicketDTO toDTO(EstadoTicket estado) {
        if (estado == null) {
            return null;
        }
        return EstadoTicketDTO.builder()
                .codigo(estado.name())
                .nombre(estado.getNombre())
                .descripcion(estado.getDescripcion())
                .build();
    }
}
