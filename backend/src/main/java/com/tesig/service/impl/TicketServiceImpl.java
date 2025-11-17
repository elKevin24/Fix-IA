package com.tesig.service.impl;

import com.tesig.dto.common.PaginatedResponseDTO;
import com.tesig.dto.ticket.*;
import com.tesig.exception.BusinessException;
import com.tesig.exception.ResourceNotFoundException;
import com.tesig.mapper.TicketMapper;
import com.tesig.model.*;
import com.tesig.repository.ClienteRepository;
import com.tesig.repository.TicketRepository;
import com.tesig.repository.UsuarioRepository;
import com.tesig.service.IEmailService;
import com.tesig.service.ITicketService;
import com.tesig.util.NumeroTicketGenerator;
import com.tesig.util.TicketEstadoValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Implementación del servicio de gestión de Tickets.
 *
 * Aplicación de principios SOLID:
 * - Single Responsibility: Solo gestiona lógica de negocio de tickets
 * - Open/Closed: Abierto a extensión (nuevos estados) mediante TicketEstadoValidator
 * - Liskov Substitution: Implementa completamente ITicketService
 * - Interface Segregation: Depende de interfaces específicas
 * - Dependency Inversion: Depende de abstracciones (interfaces)
 *
 * Pattern: Service Layer Pattern
 * Transaccionalidad: @Transactional para garantizar consistencia
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class TicketServiceImpl implements ITicketService {

    private final TicketRepository ticketRepository;
    private final ClienteRepository clienteRepository;
    private final UsuarioRepository usuarioRepository;
    private final TicketMapper ticketMapper;
    private final NumeroTicketGenerator numeroTicketGenerator;
    private final TicketEstadoValidator estadoValidator;
    private final IEmailService emailService;

    // ==================== CONSULTAS ====================

    @Override
    public PaginatedResponseDTO<TicketDTO> findAll(Pageable pageable) {
        log.debug("Buscando todos los tickets con paginación: {}", pageable);

        Page<Ticket> page = ticketRepository.findByDeletedAtIsNull(pageable);

        List<TicketDTO> tickets = page.getContent().stream()
                .map(ticketMapper::toDTO)
                .collect(Collectors.toList());

        return PaginatedResponseDTO.<TicketDTO>builder()
                .content(tickets)
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .last(page.isLast())
                .build();
    }

    @Override
    public TicketDTO findById(Long id) {
        log.debug("Buscando ticket con ID: {}", id);

        Ticket ticket = findTicketByIdOrThrow(id);
        return ticketMapper.toDTO(ticket);
    }

    @Override
    public TicketDTO findByNumeroTicket(String numeroTicket) {
        log.debug("Buscando ticket con número: {}", numeroTicket);

        Ticket ticket = ticketRepository.findByNumeroTicketAndDeletedAtIsNull(numeroTicket)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Ticket no encontrado con número: " + numeroTicket
                ));

        return ticketMapper.toDTO(ticket);
    }

    @Override
    public PaginatedResponseDTO<TicketDTO> findByCliente(Long clienteId, Pageable pageable) {
        log.debug("Buscando tickets del cliente ID: {}", clienteId);

        // Verificar que el cliente existe
        if (!clienteRepository.existsById(clienteId)) {
            throw new ResourceNotFoundException("Cliente no encontrado con ID: " + clienteId);
        }

        Page<Ticket> page = ticketRepository.findByClienteIdAndDeletedAtIsNull(clienteId, pageable);

        List<TicketDTO> tickets = page.getContent().stream()
                .map(ticketMapper::toDTO)
                .collect(Collectors.toList());

        return PaginatedResponseDTO.<TicketDTO>builder()
                .content(tickets)
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .last(page.isLast())
                .build();
    }

    @Override
    public PaginatedResponseDTO<TicketDTO> findByTecnico(Long tecnicoId, Pageable pageable) {
        log.debug("Buscando tickets del técnico ID: {}", tecnicoId);

        // Verificar que el técnico existe y es técnico
        Usuario tecnico = usuarioRepository.findById(tecnicoId)
                .orElseThrow(() -> new ResourceNotFoundException("Técnico no encontrado con ID: " + tecnicoId));

        if (tecnico.getRol() != RolUsuario.TECNICO) {
            throw new BusinessException("El usuario especificado no es un técnico");
        }

        Page<Ticket> page = ticketRepository.findByTecnicoIdAndDeletedAtIsNull(tecnicoId, pageable);

        List<TicketDTO> tickets = page.getContent().stream()
                .map(ticketMapper::toDTO)
                .collect(Collectors.toList());

        return PaginatedResponseDTO.<TicketDTO>builder()
                .content(tickets)
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .last(page.isLast())
                .build();
    }

    @Override
    public PaginatedResponseDTO<TicketDTO> findByEstado(String estado, Pageable pageable) {
        log.debug("Buscando tickets con estado: {}", estado);

        EstadoTicket estadoTicket;
        try {
            estadoTicket = EstadoTicket.valueOf(estado.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BusinessException("Estado de ticket inválido: " + estado);
        }

        Page<Ticket> page = ticketRepository.findByEstadoAndDeletedAtIsNull(estadoTicket, pageable);

        List<TicketDTO> tickets = page.getContent().stream()
                .map(ticketMapper::toDTO)
                .collect(Collectors.toList());

        return PaginatedResponseDTO.<TicketDTO>builder()
                .content(tickets)
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .last(page.isLast())
                .build();
    }

    @Override
    public PaginatedResponseDTO<TicketDTO> findActivos(Pageable pageable) {
        log.debug("Buscando tickets activos");

        Page<Ticket> page = ticketRepository.findActivosAndDeletedAtIsNull(pageable);

        List<TicketDTO> tickets = page.getContent().stream()
                .map(ticketMapper::toDTO)
                .collect(Collectors.toList());

        return PaginatedResponseDTO.<TicketDTO>builder()
                .content(tickets)
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .last(page.isLast())
                .build();
    }

    @Override
    public List<TicketDTO> search(String query) {
        log.debug("Buscando tickets con query: {}", query);

        List<Ticket> tickets = ticketRepository.search(query);

        return tickets.stream()
                .map(ticketMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public TicketEstadisticasDTO getEstadisticas() {
        log.debug("Obteniendo estadísticas de tickets");

        long totalTickets = ticketRepository.countByDeletedAtIsNull();
        long ticketsActivos = ticketRepository.countActivos();

        // Tickets por estado
        Map<String, Long> ticketsPorEstado = new HashMap<>();
        for (EstadoTicket estado : EstadoTicket.values()) {
            long count = ticketRepository.countByEstadoAndDeletedAtIsNull(estado);
            ticketsPorEstado.put(estado.getNombre(), count);
        }

        // Tickets por técnico
        Map<String, Long> ticketsPorTecnico = ticketRepository.countTicketsPorTecnico()
                .stream()
                .collect(Collectors.toMap(
                        result -> (String) result[0],
                        result -> (Long) result[1]
                ));

        // Tiempo promedio de reparación (simplificado)
        Double tiempoPromedio = ticketRepository.calcularTiempoPromedioReparacion();

        return TicketEstadisticasDTO.builder()
                .totalTickets(totalTickets)
                .ticketsActivos(ticketsActivos)
                .ticketsPorEstado(ticketsPorEstado)
                .ticketsPorTecnico(ticketsPorTecnico)
                .tiempoPromedioReparacion(tiempoPromedio != null ? tiempoPromedio : 0.0)
                .build();
    }

    // ==================== OPERACIONES DE ESTADO ====================

    @Override
    @Transactional
    public TicketDTO create(TicketCreateDTO createDTO) {
        log.info("Creando nuevo ticket para cliente ID: {}", createDTO.getClienteId());

        // Validar que el cliente existe
        Cliente cliente = clienteRepository.findById(createDTO.getClienteId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Cliente no encontrado con ID: " + createDTO.getClienteId()
                ));

        // Generar número de ticket único
        String numeroTicket = numeroTicketGenerator.generate();

        // Crear entidad
        Ticket ticket = ticketMapper.toEntity(createDTO);
        ticket.setNumeroTicket(numeroTicket);
        ticket.setCliente(cliente);
        ticket.setEstado(EstadoTicket.INGRESADO);
        // La fecha de ingreso se registra automáticamente en createdAt

        // Registrar usuario de ingreso (usuario autenticado)
        Usuario usuarioIngreso = getCurrentUser();
        ticket.setUsuarioIngreso(usuarioIngreso);

        // Guardar
        ticket = ticketRepository.save(ticket);

        log.info("Ticket creado exitosamente: {}", numeroTicket);

        // Enviar notificación por email
        try {
            emailService.enviarEmailTicketCreado(ticket);
        } catch (Exception e) {
            log.error("Error al enviar email de ticket creado, pero el ticket fue creado exitosamente", e);
            // No fallar la operación si el email falla
        }

        return ticketMapper.toDTO(ticket);
    }

    @Override
    @Transactional
    public TicketDTO asignarTecnico(Long id, AsignarTecnicoDTO asignarDTO) {
        log.info("Asignando técnico al ticket ID: {}", id);

        Ticket ticket = findTicketByIdOrThrow(id);

        // Validar estado actual
        validarEstado(ticket, EstadoTicket.INGRESADO, "asignar técnico");

        // Validar que el técnico existe y es técnico
        Usuario tecnico = usuarioRepository.findById(asignarDTO.getTecnicoId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Técnico no encontrado con ID: " + asignarDTO.getTecnicoId()
                ));

        if (tecnico.getRol() != RolUsuario.TECNICO) {
            throw new BusinessException("El usuario especificado no es un técnico");
        }

        if (!tecnico.isActivo()) {
            throw new BusinessException("El técnico no está activo");
        }

        // Realizar asignación y cambio de estado
        ticket.setTecnicoAsignado(tecnico);
        cambiarEstado(ticket, EstadoTicket.EN_DIAGNOSTICO);

        ticket = ticketRepository.save(ticket);

        log.info("Técnico {} asignado al ticket {}", tecnico.getNombreCompleto(), ticket.getNumeroTicket());
        return ticketMapper.toDTO(ticket);
    }

    @Override
    @Transactional
    public TicketDTO registrarDiagnostico(Long id, DiagnosticoDTO diagnosticoDTO) {
        log.info("Registrando diagnóstico para ticket ID: {}", id);

        Ticket ticket = findTicketByIdOrThrow(id);

        // Validar estado actual
        validarEstado(ticket, EstadoTicket.EN_DIAGNOSTICO, "registrar diagnóstico");

        // Validar que tenga técnico asignado
        if (ticket.getTecnicoAsignado() == null) {
            throw new BusinessException("El ticket debe tener un técnico asignado para registrar diagnóstico");
        }

        // Validar presupuestos
        if (diagnosticoDTO.getPresupuestoManoObra() != null && diagnosticoDTO.getPresupuestoManoObra().compareTo(BigDecimal.ZERO) < 0) {
            throw new BusinessException("El presupuesto de mano de obra no puede ser negativo");
        }
        if (diagnosticoDTO.getPresupuestoPiezas() != null && diagnosticoDTO.getPresupuestoPiezas().compareTo(BigDecimal.ZERO) < 0) {
            throw new BusinessException("El presupuesto de piezas no puede ser negativo");
        }

        // Actualizar diagnóstico y presupuesto
        ticket.setDiagnostico(diagnosticoDTO.getDiagnostico());
        ticket.setPresupuestoManoObra(diagnosticoDTO.getPresupuestoManoObra());
        ticket.setPresupuestoPiezas(diagnosticoDTO.getPresupuestoPiezas());
        ticket.setTiempoEstimadoDias(diagnosticoDTO.getTiempoEstimadoDias());
        ticket.setFechaPresupuesto(LocalDateTime.now());

        // Cambiar estado
        cambiarEstado(ticket, EstadoTicket.PRESUPUESTADO);

        ticket = ticketRepository.save(ticket);

        log.info("Diagnóstico registrado para ticket {}", ticket.getNumeroTicket());

        // Enviar notificación de presupuesto disponible
        try {
            emailService.enviarEmailPresupuestoDisponible(ticket);
        } catch (Exception e) {
            log.error("Error al enviar email de presupuesto disponible, pero el diagnóstico fue registrado", e);
            // No fallar la operación si el email falla
        }

        return ticketMapper.toDTO(ticket);
    }

    @Override
    @Transactional
    public TicketDTO aprobarPresupuesto(Long id) {
        log.info("Aprobando presupuesto de ticket ID: {}", id);

        Ticket ticket = findTicketByIdOrThrow(id);

        // Validar estado actual
        validarEstado(ticket, EstadoTicket.PRESUPUESTADO, "aprobar presupuesto");

        // Validar que tenga presupuesto registrado
        if (ticket.getPresupuestoManoObra() == null && ticket.getPresupuestoRepuestos() == null) {
            throw new BusinessException("El ticket debe tener presupuesto registrado");
        }

        // Cambiar estado
        cambiarEstado(ticket, EstadoTicket.APROBADO);

        ticket = ticketRepository.save(ticket);

        log.info("Presupuesto aprobado para ticket {}", ticket.getNumeroTicket());
        return ticketMapper.toDTO(ticket);
    }

    @Override
    @Transactional
    public TicketDTO rechazarPresupuesto(Long id, RechazarPresupuestoDTO rechazarDTO) {
        log.info("Rechazando presupuesto de ticket ID: {}", id);

        Ticket ticket = findTicketByIdOrThrow(id);

        // Validar estado actual
        validarEstado(ticket, EstadoTicket.PRESUPUESTADO, "rechazar presupuesto");

        // Validar motivo
        if (rechazarDTO.getMotivoRechazo() == null || rechazarDTO.getMotivoRechazo().isBlank()) {
            throw new BusinessException("Debe proporcionar un motivo de rechazo");
        }

        // Registrar motivo y cambiar estado
        ticket.setMotivoRechazo(rechazarDTO.getMotivoRechazo());
        cambiarEstado(ticket, EstadoTicket.RECHAZADO);

        ticket = ticketRepository.save(ticket);

        log.info("Presupuesto rechazado para ticket {}", ticket.getNumeroTicket());
        return ticketMapper.toDTO(ticket);
    }

    @Override
    @Transactional
    public TicketDTO iniciarReparacion(Long id) {
        log.info("Iniciando reparación de ticket ID: {}", id);

        Ticket ticket = findTicketByIdOrThrow(id);

        // Validar estado actual
        validarEstado(ticket, EstadoTicket.APROBADO, "iniciar reparación");

        // Validar que tenga técnico asignado
        if (ticket.getTecnicoAsignado() == null) {
            throw new BusinessException("El ticket debe tener un técnico asignado");
        }

        // Cambiar estado
        cambiarEstado(ticket, EstadoTicket.EN_REPARACION);

        ticket = ticketRepository.save(ticket);

        log.info("Reparación iniciada para ticket {}", ticket.getNumeroTicket());
        return ticketMapper.toDTO(ticket);
    }

    @Override
    @Transactional
    public TicketDTO registrarObservaciones(Long id, ObservacionesDTO observacionesDTO) {
        log.info("Registrando observaciones para ticket ID: {}", id);

        Ticket ticket = findTicketByIdOrThrow(id);

        // Validar estado
        if (ticket.getEstado() != EstadoTicket.EN_REPARACION) {
            throw new BusinessException("Solo se pueden registrar observaciones durante la reparación");
        }

        // Agregar observaciones (concatenar si ya existen)
        String observacionesActuales = ticket.getObservacionesReparacion();
        String nuevasObservaciones = observacionesDTO.getObservaciones();

        if (observacionesActuales == null || observacionesActuales.isBlank()) {
            ticket.setObservacionesReparacion(nuevasObservaciones);
        } else {
            ticket.setObservacionesReparacion(
                    observacionesActuales + "\n\n--- " + LocalDateTime.now() + " ---\n" + nuevasObservaciones
            );
        }

        ticket = ticketRepository.save(ticket);

        log.info("Observaciones registradas para ticket {}", ticket.getNumeroTicket());
        return ticketMapper.toDTO(ticket);
    }

    @Override
    @Transactional
    public TicketDTO completarReparacion(Long id) {
        log.info("Completando reparación de ticket ID: {}", id);

        Ticket ticket = findTicketByIdOrThrow(id);

        // Validar estado actual
        validarEstado(ticket, EstadoTicket.EN_REPARACION, "completar reparación");

        // Cambiar estado
        cambiarEstado(ticket, EstadoTicket.EN_PRUEBA);

        ticket = ticketRepository.save(ticket);

        log.info("Reparación completada para ticket {}", ticket.getNumeroTicket());
        return ticketMapper.toDTO(ticket);
    }

    @Override
    @Transactional
    public TicketDTO registrarPruebas(Long id, PruebasDTO pruebasDTO) {
        log.info("Registrando pruebas para ticket ID: {}", id);

        Ticket ticket = findTicketByIdOrThrow(id);

        // Validar estado actual
        validarEstado(ticket, EstadoTicket.EN_PRUEBA, "registrar pruebas");

        // Registrar resultado de pruebas
        ticket.setResultadoPruebas(pruebasDTO.getResultadoPruebas());

        // Cambiar estado según resultado
        if (Boolean.TRUE.equals(pruebasDTO.getExitoso())) {
            // Pruebas exitosas → LISTO_ENTREGA
            cambiarEstado(ticket, EstadoTicket.LISTO_ENTREGA);
            log.info("Pruebas exitosas, ticket {} listo para entrega", ticket.getNumeroTicket());
        } else {
            // Pruebas fallidas → volver a EN_REPARACION
            cambiarEstado(ticket, EstadoTicket.EN_REPARACION);
            log.info("Pruebas fallidas, ticket {} vuelve a reparación", ticket.getNumeroTicket());
        }

        ticket = ticketRepository.save(ticket);

        // Si las pruebas fueron exitosas, enviar notificación
        if (Boolean.TRUE.equals(pruebasDTO.getExitoso())) {
            try {
                emailService.enviarEmailListoParaEntrega(ticket);
            } catch (Exception e) {
                log.error("Error al enviar email de listo para entrega tras pruebas exitosas", e);
                // No fallar la operación si el email falla
            }
        }

        return ticketMapper.toDTO(ticket);
    }

    @Override
    @Transactional
    public TicketDTO marcarListoEntrega(Long id) {
        log.info("Marcando ticket ID: {} como listo para entrega", id);

        Ticket ticket = findTicketByIdOrThrow(id);

        // Validar estado actual
        validarEstado(ticket, EstadoTicket.EN_PRUEBA, "marcar listo para entrega");

        // Cambiar estado
        cambiarEstado(ticket, EstadoTicket.LISTO_ENTREGA);

        ticket = ticketRepository.save(ticket);

        log.info("Ticket {} marcado como listo para entrega", ticket.getNumeroTicket());

        // Enviar notificación de listo para entrega
        try {
            emailService.enviarEmailListoParaEntrega(ticket);
        } catch (Exception e) {
            log.error("Error al enviar email de listo para entrega, pero el estado fue actualizado", e);
            // No fallar la operación si el email falla
        }

        return ticketMapper.toDTO(ticket);
    }

    @Override
    @Transactional
    public TicketDTO entregar(Long id, EntregaDTO entregaDTO) {
        log.info("Entregando ticket ID: {}", id);

        Ticket ticket = findTicketByIdOrThrow(id);

        // Validar estado actual
        validarEstado(ticket, EstadoTicket.LISTO_ENTREGA, "entregar");

        // Registrar entrega
        ticket.setObservacionesEntrega(entregaDTO.getObservacionesEntrega());
        ticket.setFechaEntrega(LocalDateTime.now());

        // Cambiar estado (ESTADO FINAL)
        cambiarEstado(ticket, EstadoTicket.ENTREGADO);

        ticket = ticketRepository.save(ticket);

        log.info("Ticket {} entregado exitosamente", ticket.getNumeroTicket());
        return ticketMapper.toDTO(ticket);
    }

    @Override
    @Transactional
    public TicketDTO cancelar(Long id, CancelarTicketDTO cancelarDTO) {
        log.info("Cancelando ticket ID: {}", id);

        Ticket ticket = findTicketByIdOrThrow(id);

        // Validar que no esté ENTREGADO
        if (ticket.getEstado() == EstadoTicket.ENTREGADO) {
            throw new BusinessException("No se puede cancelar un ticket ya entregado");
        }

        // Validar motivo
        if (cancelarDTO.getMotivoCancelacion() == null || cancelarDTO.getMotivoCancelacion().isBlank()) {
            throw new BusinessException("Debe proporcionar un motivo de cancelación");
        }

        // Registrar motivo y cambiar estado (ESTADO FINAL)
        ticket.setMotivoCancelacion(cancelarDTO.getMotivoCancelacion());
        cambiarEstado(ticket, EstadoTicket.CANCELADO);

        ticket = ticketRepository.save(ticket);

        log.info("Ticket {} cancelado", ticket.getNumeroTicket());
        return ticketMapper.toDTO(ticket);
    }

    @Override
    public boolean puedeTransicionarA(Long id, String estadoNuevo) {
        Ticket ticket = findTicketByIdOrThrow(id);

        EstadoTicket estadoNuevoEnum;
        try {
            estadoNuevoEnum = EstadoTicket.valueOf(estadoNuevo.toUpperCase());
        } catch (IllegalArgumentException e) {
            return false;
        }

        return estadoValidator.esTransicionValida(ticket.getEstado(), estadoNuevoEnum);
    }

    // ==================== MÉTODOS HELPER PRIVADOS ====================

    /**
     * Busca un ticket por ID o lanza excepción si no existe.
     */
    private Ticket findTicketByIdOrThrow(Long id) {
        return ticketRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket no encontrado con ID: " + id));
    }

    /**
     * Valida que el ticket esté en el estado esperado.
     */
    private void validarEstado(Ticket ticket, EstadoTicket estadoEsperado, String operacion) {
        if (ticket.getEstado() != estadoEsperado) {
            throw new BusinessException(
                    String.format("No se puede %s: el ticket debe estar en estado '%s', estado actual: '%s'",
                            operacion,
                            estadoEsperado.getNombre(),
                            ticket.getEstado().getNombre()
                    )
            );
        }
    }

    /**
     * Cambia el estado del ticket validando la transición.
     */
    private void cambiarEstado(Ticket ticket, EstadoTicket nuevoEstado) {
        EstadoTicket estadoActual = ticket.getEstado();

        if (!estadoValidator.esTransicionValida(estadoActual, nuevoEstado)) {
            String mensaje = estadoValidator.getMensajeErrorTransicion(estadoActual, nuevoEstado);
            throw new BusinessException(mensaje);
        }

        log.debug("Cambiando estado de ticket {} de {} a {}",
                ticket.getNumeroTicket(),
                estadoActual.getNombre(),
                nuevoEstado.getNombre()
        );

        ticket.setEstado(nuevoEstado);
    }

    /**
     * Obtiene el usuario autenticado actual.
     */
    private Usuario getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException("Usuario autenticado no encontrado"));
    }
}
