package com.tesig.service;

import com.tesig.dto.gasto.CrearGastoDTO;
import com.tesig.dto.gasto.GastoResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface IGastoService {

    /**
     * Crea un nuevo gasto
     */
    GastoResponseDTO crear(CrearGastoDTO dto);

    /**
     * Actualiza un gasto existente
     */
    GastoResponseDTO actualizar(Long id, CrearGastoDTO dto);

    /**
     * Obtiene un gasto por ID
     */
    GastoResponseDTO getById(Long id);

    /**
     * Obtiene todos los gastos con paginación
     */
    Page<GastoResponseDTO> getAll(Pageable pageable);

    /**
     * Obtiene gastos por categoría
     */
    Page<GastoResponseDTO> getByCategoria(String categoria, Pageable pageable);

    /**
     * Obtiene gastos por rango de fechas
     */
    List<GastoResponseDTO> getByFechas(LocalDate fechaInicio, LocalDate fechaFin);

    /**
     * Calcula el total de gastos en un período
     */
    BigDecimal getTotalGastos(LocalDate fechaInicio, LocalDate fechaFin);

    /**
     * Elimina un gasto (soft delete)
     */
    void eliminar(Long id);

    /**
     * Obtiene las categorías de gastos
     */
    List<String> getCategorias();
}
