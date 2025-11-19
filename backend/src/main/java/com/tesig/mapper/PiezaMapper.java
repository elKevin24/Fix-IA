package com.tesig.mapper;

import com.tesig.dto.ActualizarPiezaDTO;
import com.tesig.dto.CrearPiezaDTO;
import com.tesig.dto.PiezaResponseDTO;
import com.tesig.model.Pieza;
import org.mapstruct.*;

import java.util.List;

/**
 * Mapper para conversiones entre entidad Pieza y DTOs.
 *
 * Aplicación de principios:
 * - Single Responsibility: Solo mapea entre Pieza y sus DTOs
 * - Dependency Inversion: MapStruct genera la implementación
 *
 * @author TESIG System
 */
@Mapper(
    componentModel = "spring",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
    unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface PiezaMapper {

    /**
     * Convierte entidad Pieza a DTO de respuesta completo.
     */
    PiezaResponseDTO toResponseDTO(Pieza pieza);

    /**
     * Convierte lista de entidades Pieza a lista de DTOs de respuesta.
     */
    List<PiezaResponseDTO> toResponseDTOList(List<Pieza> piezas);

    /**
     * Convierte DTO de creación a entidad Pieza.
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    Pieza toEntity(CrearPiezaDTO dto);

    /**
     * Actualiza una entidad Pieza existente con los datos del DTO.
     * Solo actualiza los campos no nulos del DTO.
     */
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    void updateEntityFromDTO(ActualizarPiezaDTO dto, @MappingTarget Pieza pieza);

    /**
     * Método ejecutado después del mapeo para calcular campos derivados.
     */
    @AfterMapping
    default void afterMappingResponse(@MappingTarget PiezaResponseDTO dto, Pieza pieza) {
        // Calcular campos derivados
        dto.setNecesitaReabastecimiento(pieza.necesitaReabastecimiento());
        dto.setMargenGanancia(pieza.calcularMargenGanancia());
        dto.calcularEstadoStock();
    }
}
