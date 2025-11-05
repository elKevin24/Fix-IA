package com.tesig.mapper;

import com.tesig.dto.TicketConsultaPublicaDTO;
import com.tesig.dto.ticket.TicketCreateDTO;
import com.tesig.dto.ticket.TicketDTO;
import com.tesig.model.Ticket;
import org.mapstruct.*;

/**
 * Mapper para conversiones entre entidad Ticket y DTOs.
 * MapStruct genera la implementación en tiempo de compilación.
 *
 * Aplicación de principios SOLID:
 * - Single Responsibility: Solo mapeo de Ticket
 * - Dependency Inversion: Usa otros mappers como dependencias
 * - Interface Segregation: Interface específica para Ticket
 */
@Mapper(
    componentModel = "spring",
    uses = {ClienteMapper.class, EstadoTicketMapper.class, UsuarioMapper.class},
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
    unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface TicketMapper {

    /**
     * Convierte una entidad Ticket a DTO completo.
     * Incluye todas las relaciones y campos calculados.
     */
    @Mapping(source = "cliente", target = "cliente")
    @Mapping(source = "tecnicoAsignado", target = "tecnicoAsignado")
    @Mapping(source = "usuarioIngreso", target = "usuarioIngreso")
    @Mapping(source = "estado", target = "estado")
    TicketDTO toDTO(Ticket ticket);

    /**
     * Convierte una entidad Ticket a DTO de consulta pública.
     * Solo incluye información que debe ser visible públicamente.
     */
    @Mapping(source = "createdAt", target = "fechaIngreso")
    @Mapping(source = "estado", target = "estado")
    @Mapping(source = "cliente", target = "cliente")
    TicketConsultaPublicaDTO toConsultaPublicaDTO(Ticket ticket);

    /**
     * Convierte DTO de creación a entidad Ticket.
     * No mapea cliente directamente, se hace en el servicio.
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "numeroTicket", ignore = true)
    @Mapping(target = "estado", ignore = true)
    @Mapping(target = "cliente", ignore = true)
    @Mapping(target = "tecnicoAsignado", ignore = true)
    @Mapping(target = "usuarioIngreso", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    Ticket toEntity(TicketCreateDTO dto);

    /**
     * Método auxiliar para conversiones null-safe.
     */
    @Named("nullSafeString")
    default String nullSafeString(String value) {
        return value != null ? value.trim() : null;
    }
}
