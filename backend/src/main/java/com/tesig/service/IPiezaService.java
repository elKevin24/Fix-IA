package com.tesig.service;

import com.tesig.dto.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Interfaz del servicio para gestión de piezas e inventario.
 *
 * Aplicación de principios SOLID:
 * - Dependency Inversion: Define abstracción para la lógica de negocio
 * - Interface Segregation: Métodos específicos para gestión de piezas
 * - Single Responsibility: Solo gestiona operaciones de piezas
 *
 * @author TESIG System
 */
public interface IPiezaService {

    // ==================== CRUD BÁSICO ====================

    /**
     * Crea una nueva pieza en el inventario
     */
    PiezaResponseDTO crear(CrearPiezaDTO dto);

    /**
     * Actualiza una pieza existente
     */
    PiezaResponseDTO actualizar(Long id, ActualizarPiezaDTO dto);

    /**
     * Elimina una pieza (soft delete)
     */
    void eliminar(Long id);

    /**
     * Obtiene una pieza por su ID
     */
    PiezaResponseDTO obtenerPorId(Long id);

    /**
     * Obtiene una pieza por su código único
     */
    PiezaResponseDTO obtenerPorCodigo(String codigo);

    /**
     * Lista todas las piezas activas con paginación
     */
    Page<PiezaResponseDTO> listarTodas(Pageable pageable);

    // ==================== BÚSQUEDAS ====================

    /**
     * Busca piezas por nombre (búsqueda parcial)
     */
    Page<PiezaResponseDTO> buscarPorNombre(String nombre, Pageable pageable);

    /**
     * Busca piezas por categoría
     */
    Page<PiezaResponseDTO> buscarPorCategoria(String categoria, Pageable pageable);

    /**
     * Busca piezas por marca
     */
    Page<PiezaResponseDTO> buscarPorMarca(String marca, Pageable pageable);

    /**
     * Búsqueda global por múltiples criterios
     */
    Page<PiezaResponseDTO> buscarGlobal(String busqueda, Pageable pageable);

    /**
     * Lista piezas disponibles (activas y con stock > 0)
     */
    Page<PiezaResponseDTO> listarDisponibles(Pageable pageable);

    // ==================== GESTIÓN DE STOCK ====================

    /**
     * Ajusta el stock de una pieza (entrada o salida manual)
     */
    PiezaResponseDTO ajustarStock(Long id, AjustarStockDTO dto);

    /**
     * Reduce el stock de una pieza (usado cuando se asigna a un ticket)
     */
    void reducirStock(Long id, Integer cantidad);

    /**
     * Aumenta el stock de una pieza (usado cuando se recibe inventario)
     */
    void aumentarStock(Long id, Integer cantidad);

    /**
     * Verifica si hay suficiente stock disponible
     */
    boolean verificarStockDisponible(Long id, Integer cantidadSolicitada);

    /**
     * Obtiene lista de piezas con stock bajo (stock <= stockMinimo)
     */
    List<PiezaResponseDTO> obtenerPiezasConStockBajo();

    /**
     * Obtiene lista de piezas sin stock
     */
    List<PiezaResponseDTO> obtenerPiezasSinStock();

    // ==================== CATEGORÍAS ====================

    /**
     * Obtiene todas las categorías disponibles
     */
    List<String> obtenerCategorias();
}
