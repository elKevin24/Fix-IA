package com.tesig.controller;

import com.tesig.dto.common.ApiResponse;
import com.tesig.dto.common.PaginatedResponseDTO;
import com.tesig.dto.ticket.*;
import com.tesig.service.ITicketService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para gestión de Tickets.
 *
 * Aplicación de principios SOLID:
 * - Single Responsibility: Solo maneja peticiones HTTP de tickets
 * - Dependency Inversion: Depende de ITicketService (abstracción)
 * - Open/Closed: Fácil agregar nuevos endpoints sin modificar existentes
 *
 * Permisos por rol:
 * - ADMINISTRADOR: Acceso completo
 * - RECEPCIONISTA: Crear, consultar, asignar técnico, registrar entrega
 * - TECNICO: Consultar asignados, registrar diagnóstico, reparación, pruebas
 *
 * Pattern: Controller Pattern (MVC)
 */
@RestController
@RequestMapping("/api/tickets")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Tickets", description = "Gestión de tickets de reparación")
@SecurityRequirement(name = "bearer-jwt")
public class TicketController {

    private final ITicketService ticketService;

    // ==================== CONSULTAS ====================

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECEPCIONISTA', 'TECNICO')")
    @Operation(summary = "Listar todos los tickets", description = "Obtiene todos los tickets con paginación y ordenamiento")
    public ResponseEntity<ApiResponse<PaginatedResponseDTO<TicketDTO>>> findAll(
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        log.info("GET /api/tickets - Listando tickets");
        PaginatedResponseDTO<TicketDTO> result = ticketService.findAll(pageable);
        return ResponseEntity.ok(ApiResponse.success(result, "Tickets obtenidos exitosamente"));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECEPCIONISTA', 'TECNICO')")
    @Operation(summary = "Obtener ticket por ID", description = "Obtiene un ticket específico por su ID")
    public ResponseEntity<ApiResponse<TicketDTO>> findById(@PathVariable Long id) {
        log.info("GET /api/tickets/{} - Obteniendo ticket", id);
        TicketDTO ticket = ticketService.findById(id);
        return ResponseEntity.ok(ApiResponse.success(ticket, "Ticket obtenido exitosamente"));
    }

    @GetMapping("/numero/{numeroTicket}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECEPCIONISTA', 'TECNICO')")
    @Operation(summary = "Obtener ticket por número", description = "Obtiene un ticket por su número único (TKT-YYYYMMDD-NNNN)")
    public ResponseEntity<ApiResponse<TicketDTO>> findByNumeroTicket(@PathVariable String numeroTicket) {
        log.info("GET /api/tickets/numero/{} - Obteniendo ticket por número", numeroTicket);
        TicketDTO ticket = ticketService.findByNumeroTicket(numeroTicket);
        return ResponseEntity.ok(ApiResponse.success(ticket, "Ticket obtenido exitosamente"));
    }

    @GetMapping("/cliente/{clienteId}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECEPCIONISTA')")
    @Operation(summary = "Listar tickets de un cliente", description = "Obtiene todos los tickets de un cliente específico")
    public ResponseEntity<ApiResponse<PaginatedResponseDTO<TicketDTO>>> findByCliente(
            @PathVariable Long clienteId,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        log.info("GET /api/tickets/cliente/{} - Listando tickets del cliente", clienteId);
        PaginatedResponseDTO<TicketDTO> result = ticketService.findByCliente(clienteId, pageable);
        return ResponseEntity.ok(ApiResponse.success(result, "Tickets del cliente obtenidos exitosamente"));
    }

    @GetMapping("/tecnico/{tecnicoId}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'TECNICO')")
    @Operation(summary = "Listar tickets de un técnico", description = "Obtiene todos los tickets asignados a un técnico")
    public ResponseEntity<ApiResponse<PaginatedResponseDTO<TicketDTO>>> findByTecnico(
            @PathVariable Long tecnicoId,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        log.info("GET /api/tickets/tecnico/{} - Listando tickets del técnico", tecnicoId);
        PaginatedResponseDTO<TicketDTO> result = ticketService.findByTecnico(tecnicoId, pageable);
        return ResponseEntity.ok(ApiResponse.success(result, "Tickets del técnico obtenidos exitosamente"));
    }

    @GetMapping("/estado/{estado}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECEPCIONISTA', 'TECNICO')")
    @Operation(summary = "Listar tickets por estado", description = "Obtiene todos los tickets en un estado específico")
    public ResponseEntity<ApiResponse<PaginatedResponseDTO<TicketDTO>>> findByEstado(
            @PathVariable String estado,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        log.info("GET /api/tickets/estado/{} - Listando tickets por estado", estado);
        PaginatedResponseDTO<TicketDTO> result = ticketService.findByEstado(estado, pageable);
        return ResponseEntity.ok(ApiResponse.success(result, "Tickets obtenidos exitosamente"));
    }

    @GetMapping("/activos")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECEPCIONISTA', 'TECNICO')")
    @Operation(summary = "Listar tickets activos", description = "Obtiene todos los tickets activos (no entregados ni cancelados)")
    public ResponseEntity<ApiResponse<PaginatedResponseDTO<TicketDTO>>> findActivos(
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        log.info("GET /api/tickets/activos - Listando tickets activos");
        PaginatedResponseDTO<TicketDTO> result = ticketService.findActivos(pageable);
        return ResponseEntity.ok(ApiResponse.success(result, "Tickets activos obtenidos exitosamente"));
    }

    @GetMapping("/buscar")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECEPCIONISTA', 'TECNICO')")
    @Operation(summary = "Buscar tickets", description = "Búsqueda general por número, equipo, marca, modelo, cliente")
    public ResponseEntity<ApiResponse<List<TicketDTO>>> search(@RequestParam String q) {
        log.info("GET /api/tickets/buscar?q={} - Buscando tickets", q);
        List<TicketDTO> tickets = ticketService.search(q);
        return ResponseEntity.ok(ApiResponse.success(tickets, "Búsqueda completada exitosamente"));
    }

    @GetMapping("/estadisticas")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @Operation(summary = "Obtener estadísticas", description = "Obtiene estadísticas generales de tickets")
    public ResponseEntity<ApiResponse<TicketEstadisticasDTO>> getEstadisticas() {
        log.info("GET /api/tickets/estadisticas - Obteniendo estadísticas");
        TicketEstadisticasDTO estadisticas = ticketService.getEstadisticas();
        return ResponseEntity.ok(ApiResponse.success(estadisticas, "Estadísticas obtenidas exitosamente"));
    }

    // ==================== CREACIÓN ====================

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECEPCIONISTA')")
    @Operation(summary = "Crear nuevo ticket", description = "Crea un nuevo ticket de reparación (estado inicial: INGRESADO)")
    public ResponseEntity<ApiResponse<TicketDTO>> create(@Valid @RequestBody TicketCreateDTO createDTO) {
        log.info("POST /api/tickets - Creando nuevo ticket");
        TicketDTO ticket = ticketService.create(createDTO);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(ticket, "Ticket creado exitosamente"));
    }

    // ==================== OPERACIONES DE ESTADO ====================

    @PutMapping("/{id}/asignar-tecnico")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECEPCIONISTA')")
    @Operation(
            summary = "Asignar técnico",
            description = "Asigna un técnico al ticket (INGRESADO → EN_DIAGNOSTICO)"
    )
    public ResponseEntity<ApiResponse<TicketDTO>> asignarTecnico(
            @PathVariable Long id,
            @Valid @RequestBody AsignarTecnicoDTO asignarDTO
    ) {
        log.info("PUT /api/tickets/{}/asignar-tecnico - Asignando técnico", id);
        TicketDTO ticket = ticketService.asignarTecnico(id, asignarDTO);
        return ResponseEntity.ok(ApiResponse.success(ticket, "Técnico asignado exitosamente"));
    }

    @PutMapping("/{id}/diagnostico")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'TECNICO')")
    @Operation(
            summary = "Registrar diagnóstico",
            description = "Registra diagnóstico y presupuesto (EN_DIAGNOSTICO → PRESUPUESTADO)"
    )
    public ResponseEntity<ApiResponse<TicketDTO>> registrarDiagnostico(
            @PathVariable Long id,
            @Valid @RequestBody DiagnosticoDTO diagnosticoDTO
    ) {
        log.info("PUT /api/tickets/{}/diagnostico - Registrando diagnóstico", id);
        TicketDTO ticket = ticketService.registrarDiagnostico(id, diagnosticoDTO);
        return ResponseEntity.ok(ApiResponse.success(ticket, "Diagnóstico registrado exitosamente"));
    }

    @PutMapping("/{id}/aprobar-presupuesto")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECEPCIONISTA')")
    @Operation(
            summary = "Aprobar presupuesto",
            description = "Aprueba el presupuesto del ticket (PRESUPUESTADO → APROBADO)"
    )
    public ResponseEntity<ApiResponse<TicketDTO>> aprobarPresupuesto(@PathVariable Long id) {
        log.info("PUT /api/tickets/{}/aprobar-presupuesto - Aprobando presupuesto", id);
        TicketDTO ticket = ticketService.aprobarPresupuesto(id);
        return ResponseEntity.ok(ApiResponse.success(ticket, "Presupuesto aprobado exitosamente"));
    }

    @PutMapping("/{id}/rechazar-presupuesto")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECEPCIONISTA')")
    @Operation(
            summary = "Rechazar presupuesto",
            description = "Rechaza el presupuesto del ticket (PRESUPUESTADO → RECHAZADO)"
    )
    public ResponseEntity<ApiResponse<TicketDTO>> rechazarPresupuesto(
            @PathVariable Long id,
            @Valid @RequestBody RechazarPresupuestoDTO rechazarDTO
    ) {
        log.info("PUT /api/tickets/{}/rechazar-presupuesto - Rechazando presupuesto", id);
        TicketDTO ticket = ticketService.rechazarPresupuesto(id, rechazarDTO);
        return ResponseEntity.ok(ApiResponse.success(ticket, "Presupuesto rechazado"));
    }

    @PutMapping("/{id}/iniciar-reparacion")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'TECNICO')")
    @Operation(
            summary = "Iniciar reparación",
            description = "Inicia la reparación del equipo (APROBADO → EN_REPARACION)"
    )
    public ResponseEntity<ApiResponse<TicketDTO>> iniciarReparacion(@PathVariable Long id) {
        log.info("PUT /api/tickets/{}/iniciar-reparacion - Iniciando reparación", id);
        TicketDTO ticket = ticketService.iniciarReparacion(id);
        return ResponseEntity.ok(ApiResponse.success(ticket, "Reparación iniciada exitosamente"));
    }

    @PutMapping("/{id}/observaciones")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'TECNICO')")
    @Operation(
            summary = "Registrar observaciones",
            description = "Agrega observaciones durante la reparación (estado: EN_REPARACION)"
    )
    public ResponseEntity<ApiResponse<TicketDTO>> registrarObservaciones(
            @PathVariable Long id,
            @Valid @RequestBody ObservacionesDTO observacionesDTO
    ) {
        log.info("PUT /api/tickets/{}/observaciones - Registrando observaciones", id);
        TicketDTO ticket = ticketService.registrarObservaciones(id, observacionesDTO);
        return ResponseEntity.ok(ApiResponse.success(ticket, "Observaciones registradas exitosamente"));
    }

    @PutMapping("/{id}/completar-reparacion")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'TECNICO')")
    @Operation(
            summary = "Completar reparación",
            description = "Marca la reparación como completada (EN_REPARACION → EN_PRUEBA)"
    )
    public ResponseEntity<ApiResponse<TicketDTO>> completarReparacion(@PathVariable Long id) {
        log.info("PUT /api/tickets/{}/completar-reparacion - Completando reparación", id);
        TicketDTO ticket = ticketService.completarReparacion(id);
        return ResponseEntity.ok(ApiResponse.success(ticket, "Reparación completada exitosamente"));
    }

    @PutMapping("/{id}/pruebas")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'TECNICO')")
    @Operation(
            summary = "Registrar pruebas",
            description = "Registra resultados de pruebas (EN_PRUEBA → LISTO_ENTREGA o EN_REPARACION)"
    )
    public ResponseEntity<ApiResponse<TicketDTO>> registrarPruebas(
            @PathVariable Long id,
            @Valid @RequestBody PruebasDTO pruebasDTO
    ) {
        log.info("PUT /api/tickets/{}/pruebas - Registrando pruebas", id);
        TicketDTO ticket = ticketService.registrarPruebas(id, pruebasDTO);
        return ResponseEntity.ok(ApiResponse.success(ticket, "Pruebas registradas exitosamente"));
    }

    @PutMapping("/{id}/listo-entrega")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'TECNICO')")
    @Operation(
            summary = "Marcar listo para entrega",
            description = "Marca el ticket como listo para entrega (EN_PRUEBA → LISTO_ENTREGA)"
    )
    public ResponseEntity<ApiResponse<TicketDTO>> marcarListoEntrega(@PathVariable Long id) {
        log.info("PUT /api/tickets/{}/listo-entrega - Marcando listo para entrega", id);
        TicketDTO ticket = ticketService.marcarListoEntrega(id);
        return ResponseEntity.ok(ApiResponse.success(ticket, "Ticket marcado como listo para entrega"));
    }

    @PutMapping("/{id}/entregar")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECEPCIONISTA')")
    @Operation(
            summary = "Entregar equipo",
            description = "Registra la entrega del equipo al cliente (LISTO_ENTREGA → ENTREGADO)"
    )
    public ResponseEntity<ApiResponse<TicketDTO>> entregar(
            @PathVariable Long id,
            @Valid @RequestBody EntregaDTO entregaDTO
    ) {
        log.info("PUT /api/tickets/{}/entregar - Entregando equipo", id);
        TicketDTO ticket = ticketService.entregar(id, entregaDTO);
        return ResponseEntity.ok(ApiResponse.success(ticket, "Equipo entregado exitosamente"));
    }

    @PutMapping("/{id}/cancelar")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @Operation(
            summary = "Cancelar ticket",
            description = "Cancela el ticket (cualquier estado → CANCELADO, excepto ENTREGADO)"
    )
    public ResponseEntity<ApiResponse<TicketDTO>> cancelar(
            @PathVariable Long id,
            @Valid @RequestBody CancelarTicketDTO cancelarDTO
    ) {
        log.info("PUT /api/tickets/{}/cancelar - Cancelando ticket", id);
        TicketDTO ticket = ticketService.cancelar(id, cancelarDTO);
        return ResponseEntity.ok(ApiResponse.success(ticket, "Ticket cancelado"));
    }

    // ==================== VALIDACIONES ====================

    @GetMapping("/{id}/puede-transicionar/{estadoNuevo}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECEPCIONISTA', 'TECNICO')")
    @Operation(
            summary = "Validar transición de estado",
            description = "Verifica si el ticket puede cambiar a un estado específico"
    )
    public ResponseEntity<ApiResponse<Boolean>> puedeTransicionarA(
            @PathVariable Long id,
            @PathVariable String estadoNuevo
    ) {
        log.info("GET /api/tickets/{}/puede-transicionar/{} - Validando transición", id, estadoNuevo);
        boolean puede = ticketService.puedeTransicionarA(id, estadoNuevo);
        return ResponseEntity.ok(ApiResponse.success(puede, "Validación completada"));
    }
}
