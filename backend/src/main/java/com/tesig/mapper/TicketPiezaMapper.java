package com.tesig.mapper;

import com.tesig.dto.TicketPiezaResponseDTO;
import com.tesig.model.TicketPieza;
import org.mapstruct.*;

import java.util.List;

/**
 * Mapper para conversiones entre entidad TicketPieza y DTOs.
 *
 * Aplicación de principios:
 * - Single Responsibility: Solo mapea entre TicketPieza y sus DTOs
 * - Dependency Inversion: MapStruct genera la implementación
 *
 * @author TESIG System
 */
@Mapper(
    componentModel = "spring",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
    unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface TicketPiezaMapper {

    /**
     * Convierte entidad TicketPieza a DTO de respuesta completo.
     */
    @Mapping(source = "ticket.id", target = "ticketId")
    @Mapping(source = "pieza.id", target = "piezaId")
    @Mapping(source = "pieza.codigo", target = "piezaCodigo")
    @Mapping(source = "pieza.nombre", target = "piezaNombre")
    @Mapping(source = "pieza.descripcion", target = "piezaDescripcion")
    @Mapping(source = "pieza.marca", target = "piezaMarca")
    @Mapping(source = "pieza.modelo", target = "piezaModelo")
    TicketPiezaResponseDTO toResponseDTO(TicketPieza ticketPieza);

    /**
     * Convierte lista de entidades TicketPieza a lista de DTOs de respuesta.
     */
    List<TicketPiezaResponseDTO> toResponseDTOList(List<TicketPieza> ticketPiezas);
}
