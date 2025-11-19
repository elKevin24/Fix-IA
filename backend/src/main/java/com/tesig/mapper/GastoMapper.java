package com.tesig.mapper;

import com.tesig.dto.gasto.CrearGastoDTO;
import com.tesig.dto.gasto.GastoResponseDTO;
import com.tesig.model.Gasto;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface GastoMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "categoria", expression = "java(stringToCategoria(dto.getCategoria()))")
    @Mapping(target = "metodoPago", expression = "java(stringToMetodoPago(dto.getMetodoPago()))")
    Gasto toEntity(CrearGastoDTO dto);

    @Mapping(target = "categoria", expression = "java(categoriaToString(entity.getCategoria()))")
    @Mapping(target = "metodoPago", expression = "java(metodoPagoToString(entity.getMetodoPago()))")
    GastoResponseDTO toDTO(Gasto entity);

    List<GastoResponseDTO> toDTOList(List<Gasto> entities);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "categoria", expression = "java(stringToCategoria(dto.getCategoria()))")
    @Mapping(target = "metodoPago", expression = "java(stringToMetodoPago(dto.getMetodoPago()))")
    void updateEntity(CrearGastoDTO dto, @MappingTarget Gasto entity);

    default Gasto.CategoriaGasto stringToCategoria(String categoria) {
        return categoria != null ? Gasto.CategoriaGasto.valueOf(categoria) : null;
    }

    default String categoriaToString(Gasto.CategoriaGasto categoria) {
        return categoria != null ? categoria.name() : null;
    }

    default Gasto.MetodoPago stringToMetodoPago(String metodoPago) {
        return metodoPago != null ? Gasto.MetodoPago.valueOf(metodoPago) : null;
    }

    default String metodoPagoToString(Gasto.MetodoPago metodoPago) {
        return metodoPago != null ? metodoPago.name() : null;
    }
}
