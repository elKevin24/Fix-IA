package com.tesig.service.impl;

import com.tesig.dto.AgregarPiezaTicketDTO;
import com.tesig.dto.TicketPiezaResponseDTO;
import com.tesig.exception.ResourceNotFoundException;
import com.tesig.mapper.TicketPiezaMapper;
import com.tesig.model.Pieza;
import com.tesig.model.Ticket;
import com.tesig.model.TicketPieza;
import com.tesig.repository.PiezaRepository;
import com.tesig.repository.TicketPiezaRepository;
import com.tesig.repository.TicketRepository;
import com.tesig.service.ITicketPiezaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

/**
 * Implementación del servicio de gestión de piezas asociadas a tickets.
 *
 * Aplicación de principios SOLID:
 * - Single Responsibility: Solo gestiona la relación ticket-pieza
 * - Dependency Inversion: Depende de abstracciones (interfaces)
 *
 * @author TESIG System
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class TicketPiezaServiceImpl implements ITicketPiezaService {

    private final TicketPiezaRepository ticketPiezaRepository;
    private final TicketRepository ticketRepository;
    private final PiezaRepository piezaRepository;
    private final TicketPiezaMapper ticketPiezaMapper;

    @Override
    public TicketPiezaResponseDTO agregarPiezaATicket(Long ticketId, AgregarPiezaTicketDTO dto) {
        log.info("Agregando pieza ID: {} al ticket ID: {} - Cantidad: {}",
                 dto.getPiezaId(), ticketId, dto.getCantidad());

        // Buscar ticket
        Ticket ticket = ticketRepository.findByIdAndDeletedAtIsNull(ticketId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Ticket no encontrado con ID: " + ticketId
                ));

        // Buscar pieza
        Pieza pieza = piezaRepository.findByIdAndDeletedAtIsNull(dto.getPiezaId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Pieza no encontrada con ID: " + dto.getPiezaId()
                ));

        // Validar que la pieza esté activa
        if (!pieza.getActivo()) {
            throw new IllegalStateException(
                    "La pieza " + pieza.getCodigo() + " no está activa"
            );
        }

        // Validar stock disponible
        if (!pieza.hayStockDisponible(dto.getCantidad())) {
            throw new IllegalStateException(
                    String.format("Stock insuficiente para la pieza %s. Disponible: %d, Solicitado: %d",
                                  pieza.getCodigo(), pieza.getStock(), dto.getCantidad())
            );
        }

        // Determinar precio unitario
        BigDecimal precioUnitario = dto.getPrecioUnitario() != null
                ? dto.getPrecioUnitario()
                : pieza.getPrecioVenta();

        // Crear TicketPieza
        TicketPieza ticketPieza = TicketPieza.builder()
                .ticket(ticket)
                .pieza(pieza)
                .cantidad(dto.getCantidad())
                .precioUnitario(precioUnitario)
                .notas(dto.getNotas())
                .stockDescontado(false) // Se descontará cuando se apruebe el presupuesto
                .build();

        ticketPieza.calcularSubtotal();

        ticketPieza = ticketPiezaRepository.save(ticketPieza);

        // Actualizar ticket
        ticket.agregarPieza(ticketPieza);
        ticket.actualizarPresupuestoPiezas();
        ticket.calcularPresupuestoTotal();
        ticketRepository.save(ticket);

        log.info("Pieza agregada exitosamente al ticket - Subtotal: {}",
                 ticketPieza.getSubtotal());

        return ticketPiezaMapper.toResponseDTO(ticketPieza);
    }

    @Override
    public void removerPiezaDeTicket(Long ticketPiezaId) {
        log.info("Removiendo pieza del ticket - TicketPieza ID: {}", ticketPiezaId);

        TicketPieza ticketPieza = ticketPiezaRepository.findById(ticketPiezaId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Relación ticket-pieza no encontrada con ID: " + ticketPiezaId
                ));

        // Reintegrar stock si fue descontado
        if (ticketPieza.getStockDescontado()) {
            ticketPieza.reintegrarAlInventario();
            piezaRepository.save(ticketPieza.getPieza());
            log.info("Stock reintegrado al inventario");
        }

        Ticket ticket = ticketPieza.getTicket();

        // Eliminar la pieza del ticket
        ticketPiezaRepository.delete(ticketPieza);

        // Actualizar totales del ticket
        ticket.actualizarPresupuestoPiezas();
        ticket.calcularPresupuestoTotal();
        ticketRepository.save(ticket);

        log.info("Pieza removida exitosamente del ticket");
    }

    @Override
    public TicketPiezaResponseDTO actualizarCantidad(Long ticketPiezaId, Integer nuevaCantidad) {
        log.info("Actualizando cantidad de TicketPieza ID: {} a {}", ticketPiezaId, nuevaCantidad);

        if (nuevaCantidad <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor a 0");
        }

        TicketPieza ticketPieza = ticketPiezaRepository.findById(ticketPiezaId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Relación ticket-pieza no encontrada con ID: " + ticketPiezaId
                ));

        Integer cantidadAnterior = ticketPieza.getCantidad();
        Integer diferencia = nuevaCantidad - cantidadAnterior;

        // Si el stock ya fue descontado, ajustar según la diferencia
        if (ticketPieza.getStockDescontado()) {
            if (diferencia > 0) {
                // Aumentó la cantidad, descontar más
                ticketPieza.getPieza().reducirStock(diferencia);
            } else if (diferencia < 0) {
                // Disminuyó la cantidad, reintegrar
                ticketPieza.getPieza().aumentarStock(Math.abs(diferencia));
            }
            piezaRepository.save(ticketPieza.getPieza());
        } else {
            // Si no se ha descontado, solo validar que haya stock disponible
            if (!ticketPieza.getPieza().hayStockDisponible(nuevaCantidad)) {
                throw new IllegalStateException(
                        String.format("Stock insuficiente. Disponible: %d, Solicitado: %d",
                                      ticketPieza.getPieza().getStock(), nuevaCantidad)
                );
            }
        }

        // Actualizar cantidad y recalcular subtotal
        ticketPieza.setCantidadYCalcular(nuevaCantidad);
        ticketPieza = ticketPiezaRepository.save(ticketPieza);

        // Actualizar totales del ticket
        Ticket ticket = ticketPieza.getTicket();
        ticket.actualizarPresupuestoPiezas();
        ticket.calcularPresupuestoTotal();
        ticketRepository.save(ticket);

        log.info("Cantidad actualizada - Anterior: {}, Nueva: {}, Nuevo subtotal: {}",
                 cantidadAnterior, nuevaCantidad, ticketPieza.getSubtotal());

        return ticketPiezaMapper.toResponseDTO(ticketPieza);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TicketPiezaResponseDTO> obtenerPiezasDeTicket(Long ticketId) {
        log.debug("Obteniendo piezas del ticket ID: {}", ticketId);

        List<TicketPieza> ticketPiezas = ticketPiezaRepository.findByTicketId(ticketId);
        return ticketPiezaMapper.toResponseDTOList(ticketPiezas);
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal calcularTotalPiezas(Long ticketId) {
        log.debug("Calculando total de piezas del ticket ID: {}", ticketId);

        return ticketPiezaRepository.calcularTotalPiezasPorTicket(ticketId);
    }

    @Override
    public void descontarPiezasDelInventario(Long ticketId) {
        log.info("Descontando piezas del inventario para ticket ID: {}", ticketId);

        List<TicketPieza> piezasPendientes =
                ticketPiezaRepository.findPiezasPendientesDeDescuento(ticketId);

        for (TicketPieza ticketPieza : piezasPendientes) {
            try {
                ticketPieza.descontarDelInventario();
                piezaRepository.save(ticketPieza.getPieza());
                ticketPiezaRepository.save(ticketPieza);

                log.info("Stock descontado - Pieza: {}, Cantidad: {}, Stock restante: {}",
                         ticketPieza.getPieza().getCodigo(),
                         ticketPieza.getCantidad(),
                         ticketPieza.getPieza().getStock());
            } catch (Exception e) {
                log.error("Error al descontar pieza del inventario", e);
                throw new IllegalStateException(
                        "Error al descontar pieza " + ticketPieza.getPieza().getCodigo() +
                        " del inventario: " + e.getMessage()
                );
            }
        }

        log.info("Todas las piezas descontadas exitosamente del inventario");
    }

    @Override
    public void reintegrarPiezasAlInventario(Long ticketId) {
        log.info("Reintegrando piezas al inventario para ticket ID: {}", ticketId);

        List<TicketPieza> piezasDescontadas =
                ticketPiezaRepository.findPiezasDescontadas(ticketId);

        for (TicketPieza ticketPieza : piezasDescontadas) {
            try {
                ticketPieza.reintegrarAlInventario();
                piezaRepository.save(ticketPieza.getPieza());
                ticketPiezaRepository.save(ticketPieza);

                log.info("Stock reintegrado - Pieza: {}, Cantidad: {}, Stock actual: {}",
                         ticketPieza.getPieza().getCodigo(),
                         ticketPieza.getCantidad(),
                         ticketPieza.getPieza().getStock());
            } catch (Exception e) {
                log.error("Error al reintegrar pieza al inventario", e);
                // Continuar con las demás piezas aunque una falle
            }
        }

        log.info("Todas las piezas reintegradas exitosamente al inventario");
    }
}
