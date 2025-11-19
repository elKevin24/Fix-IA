package com.tesig.service.impl;

import com.tesig.dto.*;
import com.tesig.exception.ResourceNotFoundException;
import com.tesig.mapper.PiezaMapper;
import com.tesig.model.Pieza;
import com.tesig.repository.PiezaRepository;
import com.tesig.service.IPiezaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Implementación del servicio de gestión de piezas e inventario.
 *
 * Aplicación de principios SOLID:
 * - Single Responsibility: Solo gestiona la lógica de negocio de piezas
 * - Open/Closed: Abierto a extensión mediante interfaces
 * - Dependency Inversion: Depende de abstracciones (interfaces)
 *
 * @author TESIG System
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class PiezaServiceImpl implements IPiezaService {

    private final PiezaRepository piezaRepository;
    private final PiezaMapper piezaMapper;

    // ==================== CRUD BÁSICO ====================

    @Override
    public PiezaResponseDTO crear(CrearPiezaDTO dto) {
        log.info("Creando nueva pieza con código: {}", dto.getCodigo());

        // Validar que no existe una pieza con el mismo código
        if (piezaRepository.existsByCodigoAndDeletedAtIsNull(dto.getCodigo())) {
            throw new IllegalArgumentException(
                    "Ya existe una pieza con el código: " + dto.getCodigo()
            );
        }

        // Validar que el precio de venta sea mayor o igual al costo
        if (dto.getPrecioVenta().compareTo(dto.getPrecioCosto()) < 0) {
            throw new IllegalArgumentException(
                    "El precio de venta debe ser mayor o igual al precio de costo"
            );
        }

        // Convertir DTO a entidad
        Pieza pieza = piezaMapper.toEntity(dto);

        // Guardar
        pieza = piezaRepository.save(pieza);

        log.info("Pieza creada exitosamente con ID: {} y código: {}",
                 pieza.getId(), pieza.getCodigo());

        return piezaMapper.toResponseDTO(pieza);
    }

    @Override
    public PiezaResponseDTO actualizar(Long id, ActualizarPiezaDTO dto) {
        log.info("Actualizando pieza con ID: {}", id);

        Pieza pieza = buscarPiezaPorId(id);

        // Validar código único si se está actualizando
        if (dto.getCodigo() != null && !dto.getCodigo().equals(pieza.getCodigo())) {
            if (piezaRepository.existsByCodigoAndIdNot(dto.getCodigo(), id)) {
                throw new IllegalArgumentException(
                        "Ya existe una pieza con el código: " + dto.getCodigo()
                );
            }
        }

        // Actualizar campos
        piezaMapper.updateEntityFromDTO(dto, pieza);

        // Validar precios si se actualizaron
        if (pieza.getPrecioVenta().compareTo(pieza.getPrecioCosto()) < 0) {
            throw new IllegalArgumentException(
                    "El precio de venta debe ser mayor o igual al precio de costo"
            );
        }

        pieza = piezaRepository.save(pieza);

        log.info("Pieza actualizada exitosamente: {}", id);

        return piezaMapper.toResponseDTO(pieza);
    }

    @Override
    public void eliminar(Long id) {
        log.info("Eliminando pieza con ID: {}", id);

        Pieza pieza = buscarPiezaPorId(id);
        pieza.marcarComoEliminada();
        piezaRepository.save(pieza);

        log.info("Pieza eliminada exitosamente: {}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public PiezaResponseDTO obtenerPorId(Long id) {
        log.debug("Obteniendo pieza con ID: {}", id);

        Pieza pieza = buscarPiezaPorId(id);
        return piezaMapper.toResponseDTO(pieza);
    }

    @Override
    @Transactional(readOnly = true)
    public PiezaResponseDTO obtenerPorCodigo(String codigo) {
        log.debug("Obteniendo pieza con código: {}", codigo);

        Pieza pieza = piezaRepository.findByCodigoAndDeletedAtIsNull(codigo)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Pieza no encontrada con código: " + codigo
                ));

        return piezaMapper.toResponseDTO(pieza);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PiezaResponseDTO> listarTodas(Pageable pageable) {
        log.debug("Listando todas las piezas activas - Página: {}", pageable.getPageNumber());

        Page<Pieza> piezas = piezaRepository.findAllActive(pageable);
        return piezas.map(piezaMapper::toResponseDTO);
    }

    // ==================== BÚSQUEDAS ====================

    @Override
    @Transactional(readOnly = true)
    public Page<PiezaResponseDTO> buscarPorNombre(String nombre, Pageable pageable) {
        log.debug("Buscando piezas por nombre: {}", nombre);

        Page<Pieza> piezas = piezaRepository.findByNombreContainingIgnoreCase(nombre, pageable);
        return piezas.map(piezaMapper::toResponseDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PiezaResponseDTO> buscarPorCategoria(String categoria, Pageable pageable) {
        log.debug("Buscando piezas por categoría: {}", categoria);

        Page<Pieza> piezas = piezaRepository.findByCategoria(categoria, pageable);
        return piezas.map(piezaMapper::toResponseDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PiezaResponseDTO> buscarPorMarca(String marca, Pageable pageable) {
        log.debug("Buscando piezas por marca: {}", marca);

        Page<Pieza> piezas = piezaRepository.findByMarcaContainingIgnoreCase(marca, pageable);
        return piezas.map(piezaMapper::toResponseDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PiezaResponseDTO> buscarGlobal(String busqueda, Pageable pageable) {
        log.debug("Búsqueda global de piezas: {}", busqueda);

        Page<Pieza> piezas = piezaRepository.buscarGlobal(busqueda, pageable);
        return piezas.map(piezaMapper::toResponseDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PiezaResponseDTO> listarDisponibles(Pageable pageable) {
        log.debug("Listando piezas disponibles (activas y con stock)");

        Page<Pieza> piezas = piezaRepository.findPiezasDisponibles(pageable);
        return piezas.map(piezaMapper::toResponseDTO);
    }

    // ==================== GESTIÓN DE STOCK ====================

    @Override
    public PiezaResponseDTO ajustarStock(Long id, AjustarStockDTO dto) {
        log.info("Ajustando stock de pieza ID: {} - Tipo: {}, Cantidad: {}",
                 id, dto.getTipoMovimiento(), dto.getCantidad());

        Pieza pieza = buscarPiezaPorId(id);

        if ("ENTRADA".equals(dto.getTipoMovimiento())) {
            pieza.aumentarStock(dto.getCantidad());
            log.info("Stock aumentado - Pieza: {}, Cantidad: {}, Nuevo stock: {}",
                     pieza.getCodigo(), dto.getCantidad(), pieza.getStock());
        } else if ("SALIDA".equals(dto.getTipoMovimiento())) {
            pieza.reducirStock(dto.getCantidad());
            log.info("Stock reducido - Pieza: {}, Cantidad: {}, Nuevo stock: {}",
                     pieza.getCodigo(), dto.getCantidad(), pieza.getStock());
        } else {
            throw new IllegalArgumentException(
                    "Tipo de movimiento inválido: " + dto.getTipoMovimiento()
            );
        }

        pieza = piezaRepository.save(pieza);

        // TODO: Registrar el movimiento en MovimientoInventario cuando se implemente

        return piezaMapper.toResponseDTO(pieza);
    }

    @Override
    public void reducirStock(Long id, Integer cantidad) {
        log.info("Reduciendo stock de pieza ID: {} - Cantidad: {}", id, cantidad);

        Pieza pieza = buscarPiezaPorId(id);
        pieza.reducirStock(cantidad);
        piezaRepository.save(pieza);

        log.info("Stock reducido exitosamente - Nuevo stock: {}", pieza.getStock());
    }

    @Override
    public void aumentarStock(Long id, Integer cantidad) {
        log.info("Aumentando stock de pieza ID: {} - Cantidad: {}", id, cantidad);

        Pieza pieza = buscarPiezaPorId(id);
        pieza.aumentarStock(cantidad);
        piezaRepository.save(pieza);

        log.info("Stock aumentado exitosamente - Nuevo stock: {}", pieza.getStock());
    }

    @Override
    @Transactional(readOnly = true)
    public boolean verificarStockDisponible(Long id, Integer cantidadSolicitada) {
        log.debug("Verificando stock disponible - Pieza ID: {}, Cantidad solicitada: {}",
                  id, cantidadSolicitada);

        Pieza pieza = buscarPiezaPorId(id);
        return pieza.hayStockDisponible(cantidadSolicitada);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PiezaResponseDTO> obtenerPiezasConStockBajo() {
        log.debug("Obteniendo piezas con stock bajo");

        List<Pieza> piezas = piezaRepository.findPiezasConStockBajo();
        return piezaMapper.toResponseDTOList(piezas);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PiezaResponseDTO> obtenerPiezasSinStock() {
        log.debug("Obteniendo piezas sin stock");

        List<Pieza> piezas = piezaRepository.findPiezasSinStock();
        return piezaMapper.toResponseDTOList(piezas);
    }

    // ==================== CATEGORÍAS ====================

    @Override
    @Transactional(readOnly = true)
    public List<String> obtenerCategorias() {
        log.debug("Obteniendo todas las categorías");
        return piezaRepository.findAllCategorias();
    }

    // ==================== MÉTODOS AUXILIARES ====================

    /**
     * Busca una pieza por ID y lanza excepción si no existe
     */
    private Pieza buscarPiezaPorId(Long id) {
        return piezaRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Pieza no encontrada con ID: " + id
                ));
    }
}
