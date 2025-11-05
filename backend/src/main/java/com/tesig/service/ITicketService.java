package com.tesig.service;

import com.tesig.dto.common.PaginatedResponseDTO;
import com.tesig.dto.ticket.*;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Interface de servicio para gestión de Tickets.
 *
 * Aplicación de principios SOLID:
 * - Single Responsibility: Solo define operaciones de tickets
 * - Interface Segregation: Interface cohesiva con operaciones relacionadas
 * - Dependency Inversion: Los clientes dependen de esta abstracción
 *
 * Cada método corresponde a una transición de estado específica o consulta.
 */
public interface ITicketService {

    // ==================== CONSULTAS ====================

    /**
     * Obtiene todos los tickets con paginación y ordenamiento.
     *
     * @param pageable Información de paginación y ordenamiento
     * @return Respuesta paginada con tickets
     */
    PaginatedResponseDTO<TicketDTO> findAll(Pageable pageable);

    /**
     * Busca un ticket por ID.
     *
     * @param id ID del ticket
     * @return DTO del ticket
     * @throws com.tesig.exception.ResourceNotFoundException si no existe
     */
    TicketDTO findById(Long id);

    /**
     * Busca un ticket por número de ticket.
     *
     * @param numeroTicket Número de ticket (formato: TKT-YYYYMMDD-NNNN)
     * @return DTO del ticket
     * @throws com.tesig.exception.ResourceNotFoundException si no existe
     */
    TicketDTO findByNumeroTicket(String numeroTicket);

    /**
     * Busca tickets por cliente.
     *
     * @param clienteId ID del cliente
     * @param pageable Información de paginación
     * @return Respuesta paginada con tickets del cliente
     */
    PaginatedResponseDTO<TicketDTO> findByCliente(Long clienteId, Pageable pageable);

    /**
     * Busca tickets asignados a un técnico.
     *
     * @param tecnicoId ID del técnico
     * @param pageable Información de paginación
     * @return Respuesta paginada con tickets del técnico
     */
    PaginatedResponseDTO<TicketDTO> findByTecnico(Long tecnicoId, Pageable pageable);

    /**
     * Busca tickets por estado.
     *
     * @param estado Estado del ticket
     * @param pageable Información de paginación
     * @return Respuesta paginada con tickets del estado
     */
    PaginatedResponseDTO<TicketDTO> findByEstado(String estado, Pageable pageable);

    /**
     * Busca tickets activos (no entregados ni cancelados).
     *
     * @param pageable Información de paginación
     * @return Respuesta paginada con tickets activos
     */
    PaginatedResponseDTO<TicketDTO> findActivos(Pageable pageable);

    /**
     * Búsqueda general por múltiples campos.
     *
     * @param query Texto de búsqueda
     * @return Lista de tickets que coinciden
     */
    List<TicketDTO> search(String query);

    /**
     * Obtiene estadísticas de tickets.
     *
     * @return DTO con estadísticas
     */
    TicketEstadisticasDTO getEstadisticas();

    // ==================== OPERACIONES DE ESTADO ====================

    /**
     * Crea un nuevo ticket (INGRESADO).
     *
     * Estado inicial: INGRESADO
     *
     * @param createDTO Datos de creación
     * @return Ticket creado
     */
    TicketDTO create(TicketCreateDTO createDTO);

    /**
     * Asigna un técnico al ticket (INGRESADO → EN_DIAGNOSTICO).
     *
     * Validaciones:
     * - Estado actual debe ser INGRESADO
     * - Técnico debe existir y tener rol TECNICO
     *
     * @param id ID del ticket
     * @param asignarDTO Datos de asignación
     * @return Ticket actualizado
     */
    TicketDTO asignarTecnico(Long id, AsignarTecnicoDTO asignarDTO);

    /**
     * Registra diagnóstico y presupuesto (EN_DIAGNOSTICO → PRESUPUESTADO).
     *
     * Validaciones:
     * - Estado actual debe ser EN_DIAGNOSTICO
     * - Debe tener técnico asignado
     * - Presupuestos deben ser >= 0
     *
     * @param id ID del ticket
     * @param diagnosticoDTO Datos de diagnóstico
     * @return Ticket actualizado
     */
    TicketDTO registrarDiagnostico(Long id, DiagnosticoDTO diagnosticoDTO);

    /**
     * Aprueba el presupuesto (PRESUPUESTADO → APROBADO).
     *
     * Validaciones:
     * - Estado actual debe ser PRESUPUESTADO
     * - Debe tener presupuesto registrado
     *
     * @param id ID del ticket
     * @return Ticket actualizado
     */
    TicketDTO aprobarPresupuesto(Long id);

    /**
     * Rechaza el presupuesto (PRESUPUESTADO → RECHAZADO).
     *
     * Validaciones:
     * - Estado actual debe ser PRESUPUESTADO
     * - Debe proporcionar motivo de rechazo
     *
     * @param id ID del ticket
     * @param rechazarDTO Datos de rechazo
     * @return Ticket actualizado
     */
    TicketDTO rechazarPresupuesto(Long id, RechazarPresupuestoDTO rechazarDTO);

    /**
     * Inicia la reparación (APROBADO → EN_REPARACION).
     *
     * Validaciones:
     * - Estado actual debe ser APROBADO
     * - Debe tener técnico asignado
     *
     * @param id ID del ticket
     * @return Ticket actualizado
     */
    TicketDTO iniciarReparacion(Long id);

    /**
     * Registra observaciones durante la reparación.
     * Estado debe ser EN_REPARACION.
     *
     * @param id ID del ticket
     * @param observacionesDTO Observaciones
     * @return Ticket actualizado
     */
    TicketDTO registrarObservaciones(Long id, ObservacionesDTO observacionesDTO);

    /**
     * Completa la reparación (EN_REPARACION → EN_PRUEBA).
     *
     * Validaciones:
     * - Estado actual debe ser EN_REPARACION
     *
     * @param id ID del ticket
     * @return Ticket actualizado
     */
    TicketDTO completarReparacion(Long id);

    /**
     * Registra resultados de pruebas (EN_PRUEBA → LISTO_ENTREGA o EN_REPARACION).
     *
     * Validaciones:
     * - Estado actual debe ser EN_PRUEBA
     * - Si exitoso: → LISTO_ENTREGA
     * - Si no exitoso: → EN_REPARACION (para nuevas correcciones)
     *
     * @param id ID del ticket
     * @param pruebasDTO Resultados de pruebas
     * @return Ticket actualizado
     */
    TicketDTO registrarPruebas(Long id, PruebasDTO pruebasDTO);

    /**
     * Marca el ticket como listo para entrega (se usa si ya está EN_PRUEBA y exitoso).
     * EN_PRUEBA → LISTO_ENTREGA
     *
     * @param id ID del ticket
     * @return Ticket actualizado
     */
    TicketDTO marcarListoEntrega(Long id);

    /**
     * Entrega el equipo al cliente (LISTO_ENTREGA → ENTREGADO).
     *
     * Validaciones:
     * - Estado actual debe ser LISTO_ENTREGA
     *
     * @param id ID del ticket
     * @param entregaDTO Datos de entrega
     * @return Ticket actualizado (ESTADO FINAL)
     */
    TicketDTO entregar(Long id, EntregaDTO entregaDTO);

    /**
     * Cancela el ticket.
     *
     * Puede hacerse desde cualquier estado excepto ENTREGADO.
     * Requiere motivo de cancelación.
     *
     * @param id ID del ticket
     * @param cancelarDTO Datos de cancelación
     * @return Ticket cancelado (ESTADO FINAL)
     */
    TicketDTO cancelar(Long id, CancelarTicketDTO cancelarDTO);

    /**
     * Verifica si un ticket puede cambiar a un estado específico.
     *
     * @param id ID del ticket
     * @param estadoNuevo Estado objetivo
     * @return true si la transición es válida
     */
    boolean puedeTransicionarA(Long id, String estadoNuevo);
}
