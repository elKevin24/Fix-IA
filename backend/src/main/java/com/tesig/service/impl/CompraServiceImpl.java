package com.tesig.service.impl;

import com.tesig.dto.compra.CompraResponseDTO;
import com.tesig.dto.compra.CrearCompraDTO;
import com.tesig.exception.ResourceNotFoundException;
import com.tesig.exception.BusinessException;
import com.tesig.mapper.CompraMapper;
import com.tesig.model.*;
import com.tesig.repository.*;
import com.tesig.service.ICompraService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class CompraServiceImpl implements ICompraService {

    private final CompraRepository compraRepository;
    private final CompraDetalleRepository compraDetalleRepository;
    private final PiezaRepository piezaRepository;
    private final MovimientoInventarioRepository movimientoRepository;
    private final CompraMapper compraMapper;

    @Override
    public CompraResponseDTO crear(CrearCompraDTO dto) {
        Compra compra = compraMapper.toEntity(dto);
        compra.setCodigoCompra(generarCodigoCompra());
        compra.setEstado(Compra.EstadoCompra.PENDIENTE);

        BigDecimal total = BigDecimal.ZERO;

        // Procesar detalles
        for (CrearCompraDTO.CompraDetalleDTO detalleDTO : dto.getDetalles()) {
            Pieza pieza = piezaRepository.findByIdAndNotDeleted(detalleDTO.getPiezaId())
                    .orElseThrow(() -> new ResourceNotFoundException("Pieza no encontrada: " + detalleDTO.getPiezaId()));

            CompraDetalle detalle = CompraDetalle.builder()
                    .compra(compra)
                    .pieza(pieza)
                    .cantidad(detalleDTO.getCantidad())
                    .precioUnitario(detalleDTO.getPrecioUnitario())
                    .subtotal(detalleDTO.getPrecioUnitario().multiply(BigDecimal.valueOf(detalleDTO.getCantidad())))
                    .build();

            compra.getDetalles().add(detalle);
            total = total.add(detalle.getSubtotal());
        }

        compra.setTotal(total);
        Compra saved = compraRepository.save(compra);
        return compraMapper.toDTO(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public CompraResponseDTO getById(Long id) {
        Compra compra = compraRepository.findByIdAndNotDeleted(id)
                .orElseThrow(() -> new ResourceNotFoundException("Compra no encontrada: " + id));
        return compraMapper.toDTO(compra);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CompraResponseDTO> getAll(Pageable pageable) {
        return compraRepository.findAllNotDeleted(pageable)
                .map(compraMapper::toDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CompraResponseDTO> buscarPorProveedor(String proveedor, Pageable pageable) {
        return compraRepository.findByProveedorContaining(proveedor, pageable)
                .map(compraMapper::toDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CompraResponseDTO> getByEstado(String estado, Pageable pageable) {
        Compra.EstadoCompra estadoEnum = Compra.EstadoCompra.valueOf(estado);
        return compraRepository.findByEstado(estadoEnum, pageable)
                .map(compraMapper::toDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CompraResponseDTO> getByFechas(LocalDate fechaInicio, LocalDate fechaFin) {
        return compraRepository.findByFechaCompraBetween(fechaInicio, fechaFin)
                .stream()
                .map(compraMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public CompraResponseDTO recibirCompra(Long id) {
        Compra compra = compraRepository.findByIdAndNotDeleted(id)
                .orElseThrow(() -> new ResourceNotFoundException("Compra no encontrada: " + id));

        if (compra.getEstado() != Compra.EstadoCompra.PENDIENTE) {
            throw new BusinessException("Solo se pueden recibir compras en estado PENDIENTE");
        }

        // Actualizar inventario para cada detalle
        for (CompraDetalle detalle : compra.getDetalles()) {
            Pieza pieza = detalle.getPieza();
            int stockAnterior = pieza.getStock();
            int nuevoStock = stockAnterior + detalle.getCantidad();

            // Actualizar stock de la pieza
            pieza.setStock(nuevoStock);

            // Actualizar precio de costo si cambió
            if (detalle.getPrecioUnitario().compareTo(pieza.getPrecioCosto()) != 0) {
                pieza.setPrecioCosto(detalle.getPrecioUnitario());
            }

            piezaRepository.save(pieza);

            // Registrar movimiento de inventario
            MovimientoInventario movimiento = MovimientoInventario.builder()
                    .pieza(pieza)
                    .tipoMovimiento(MovimientoInventario.TipoMovimiento.COMPRA)
                    .cantidad(detalle.getCantidad())
                    .stockAnterior(stockAnterior)
                    .stockNuevo(nuevoStock)
                    .descripcion("Compra recibida: " + compra.getCodigoCompra())
                    .compra(compra)
                    .build();
            movimientoRepository.save(movimiento);
        }

        compra.setEstado(Compra.EstadoCompra.RECIBIDA);
        Compra saved = compraRepository.save(compra);
        return compraMapper.toDTO(saved);
    }

    @Override
    public CompraResponseDTO cancelarCompra(Long id) {
        Compra compra = compraRepository.findByIdAndNotDeleted(id)
                .orElseThrow(() -> new ResourceNotFoundException("Compra no encontrada: " + id));

        if (compra.getEstado() == Compra.EstadoCompra.RECIBIDA) {
            throw new BusinessException("No se puede cancelar una compra ya recibida");
        }

        compra.setEstado(Compra.EstadoCompra.CANCELADA);
        Compra saved = compraRepository.save(compra);
        return compraMapper.toDTO(saved);
    }

    @Override
    public void eliminar(Long id) {
        Compra compra = compraRepository.findByIdAndNotDeleted(id)
                .orElseThrow(() -> new ResourceNotFoundException("Compra no encontrada: " + id));

        if (compra.getEstado() == Compra.EstadoCompra.RECIBIDA) {
            throw new BusinessException("No se puede eliminar una compra ya recibida");
        }

        compra.setDeleted(true);
        compraRepository.save(compra);
    }

    private String generarCodigoCompra() {
        String fecha = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String prefijo = "TES-CMP-" + fecha + "-";

        // Buscar última compra del día
        long count = compraRepository.findAll().stream()
                .filter(c -> c.getCodigoCompra() != null && c.getCodigoCompra().startsWith(prefijo))
                .count();

        return prefijo + String.format("%04d", count + 1);
    }
}
