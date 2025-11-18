package com.tesig.mapper;

import com.tesig.dto.compra.CompraResponseDTO;
import com.tesig.dto.compra.CrearCompraDTO;
import com.tesig.model.Compra;
import com.tesig.model.CompraDetalle;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CompraMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "codigoCompra", ignore = true)
    @Mapping(target = "total", ignore = true)
    @Mapping(target = "estado", ignore = true)
    @Mapping(target = "detalles", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Compra toEntity(CrearCompraDTO dto);

    @Mapping(target = "estado", source = "estado")
    @Mapping(target = "detalles", source = "detalles")
    CompraResponseDTO toDTO(Compra entity);

    List<CompraResponseDTO> toDTOList(List<Compra> entities);

    @Mapping(target = "piezaId", source = "pieza.id")
    @Mapping(target = "piezaCodigo", source = "pieza.codigo")
    @Mapping(target = "piezaNombre", source = "pieza.nombre")
    CompraResponseDTO.DetalleDTO detalleToDTO(CompraDetalle detalle);

    default String estadoToString(Compra.EstadoCompra estado) {
        return estado != null ? estado.name() : null;
    }
}
