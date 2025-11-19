package com.tesig.service;

import com.tesig.dto.compra.CompraResponseDTO;
import com.tesig.dto.compra.CrearCompraDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

public interface ICompraService {

    /**
     * Crea una nueva compra con sus detalles
     */
    CompraResponseDTO crear(CrearCompraDTO dto);

    /**
     * Obtiene una compra por ID
     */
    CompraResponseDTO getById(Long id);

    /**
     * Obtiene todas las compras con paginación
     */
    Page<CompraResponseDTO> getAll(Pageable pageable);

    /**
     * Busca compras por proveedor
     */
    Page<CompraResponseDTO> buscarPorProveedor(String proveedor, Pageable pageable);

    /**
     * Obtiene compras por estado
     */
    Page<CompraResponseDTO> getByEstado(String estado, Pageable pageable);

    /**
     * Obtiene compras por rango de fechas
     */
    List<CompraResponseDTO> getByFechas(LocalDate fechaInicio, LocalDate fechaFin);

    /**
     * Recibe una compra (cambia estado a RECIBIDA y actualiza inventario)
     */
    CompraResponseDTO recibirCompra(Long id);

    /**
     * Cancela una compra
     */
    CompraResponseDTO cancelarCompra(Long id);

    /**
     * Elimina una compra (soft delete)
     */
    void eliminar(Long id);
}
