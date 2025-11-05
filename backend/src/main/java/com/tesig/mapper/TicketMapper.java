package com.tesig.mapper;

import com.tesig.dto.TicketConsultaPublicaDTO;
import com.tesig.model.Ticket;
import org.mapstruct.*;

/**
 * Mapper para conversiones entre entidad Ticket y DTOs.
 * MapStruct genera la implementación en tiempo de compilación.
 */
@Mapper(
    componentModel = "spring",
    uses = {ClienteMapper.class, EstadoTicketMapper.class},
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
    unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface TicketMapper {

    /**
     * Convierte una entidad Ticket a DTO de consulta pública.
     * Solo incluye información que debe ser visible públicamente.
     */
    @Mapping(source = "createdAt", target = "fechaIngreso")
    @Mapping(source = "estado", target = "estado")
    @Mapping(source = "cliente", target = "cliente")
    TicketConsultaPublicaDTO toConsultaPublicaDTO(Ticket ticket);
}
