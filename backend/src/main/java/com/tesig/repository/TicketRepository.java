package com.tesig.repository;

import com.tesig.model.EstadoTicket;
import com.tesig.model.Ticket;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repositorio para la entidad Ticket.
 *
 * Aplicación de principios SOLID:
 * - Single Responsibility: Solo operaciones de persistencia de tickets
 * - Interface Segregation: Métodos específicos para cada necesidad
 * - Dependency Inversion: Abstracción para acceso a datos
 */
@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {

    // ==================== BÚSQUEDAS BÁSICAS ====================

    /**
     * Busca un ticket por ID excluyendo eliminados.
     */
    Optional<Ticket> findByIdAndDeletedAtIsNull(Long id);

    /**
     * Busca un ticket por número de ticket (para consulta pública).
     */
    Optional<Ticket> findByNumeroTicket(String numeroTicket);

    /**
     * Busca un ticket por número de ticket excluyendo eliminados.
     */
    Optional<Ticket> findByNumeroTicketAndDeletedAtIsNull(String numeroTicket);

    /**
     * Verifica si existe un número de ticket.
     */
    boolean existsByNumeroTicket(String numeroTicket);

    // ==================== BÚSQUEDAS CON PAGINACIÓN ====================

    /**
     * Obtiene todos los tickets no eliminados con paginación.
     */
    Page<Ticket> findByDeletedAtIsNull(Pageable pageable);

    /**
     * Busca tickets por cliente con paginación.
     */
    Page<Ticket> findByClienteIdAndDeletedAtIsNull(Long clienteId, Pageable pageable);

    /**
     * Busca tickets por técnico asignado con paginación.
     */
    Page<Ticket> findByTecnicoAsignadoIdAndDeletedAtIsNull(Long tecnicoId, Pageable pageable);

    /**
     * Busca tickets por estado con paginación.
     */
    Page<Ticket> findByEstadoAndDeletedAtIsNull(EstadoTicket estado, Pageable pageable);

    /**
     * Busca tickets activos (no entregados ni cancelados) con paginación.
     */
    @Query("SELECT t FROM Ticket t WHERE t.deletedAt IS NULL AND " +
           "t.estado NOT IN (com.tesig.model.EstadoTicket.ENTREGADO, com.tesig.model.EstadoTicket.CANCELADO) " +
           "ORDER BY t.createdAt DESC")
    Page<Ticket> findActivosAndDeletedAtIsNull(Pageable pageable);

    // ==================== BÚSQUEDAS SIN PAGINACIÓN ====================

    /**
     * Busca tickets por cliente sin paginación.
     */
    @Query("SELECT t FROM Ticket t WHERE t.cliente.id = :clienteId AND t.deletedAt IS NULL ORDER BY t.createdAt DESC")
    List<Ticket> findByClienteId(@Param("clienteId") Long clienteId);

    /**
     * Busca tickets por técnico asignado sin paginación.
     */
    @Query("SELECT t FROM Ticket t WHERE t.tecnicoAsignado.id = :tecnicoId AND t.deletedAt IS NULL ORDER BY t.createdAt DESC")
    List<Ticket> findByTecnicoAsignadoId(@Param("tecnicoId") Long tecnicoId);

    /**
     * Busca tickets por estado sin paginación.
     */
    @Query("SELECT t FROM Ticket t WHERE t.estado = :estado AND t.deletedAt IS NULL ORDER BY t.createdAt DESC")
    List<Ticket> findByEstado(@Param("estado") EstadoTicket estado);

    /**
     * Busca tickets activos sin paginación.
     */
    @Query("SELECT t FROM Ticket t WHERE t.deletedAt IS NULL AND " +
           "t.estado NOT IN (com.tesig.model.EstadoTicket.ENTREGADO, com.tesig.model.EstadoTicket.CANCELADO) " +
           "ORDER BY t.createdAt DESC")
    List<Ticket> findTicketsActivos();

    /**
     * Busca tickets por rango de fechas.
     */
    @Query("SELECT t FROM Ticket t WHERE t.deletedAt IS NULL AND " +
           "t.createdAt BETWEEN :fechaInicio AND :fechaFin " +
           "ORDER BY t.createdAt DESC")
    List<Ticket> findByFechaCreacionBetween(
        @Param("fechaInicio") LocalDateTime fechaInicio,
        @Param("fechaFin") LocalDateTime fechaFin
    );

    /**
     * Búsqueda general por múltiples campos.
     * Busca en: número de ticket, tipo de equipo, marca, modelo, falla reportada.
     */
    @Query("SELECT t FROM Ticket t WHERE t.deletedAt IS NULL AND " +
           "(LOWER(t.numeroTicket) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(t.tipoEquipo) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(t.marca) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(t.modelo) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(t.fallaReportada) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(t.cliente.nombre) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(t.cliente.apellido) LIKE LOWER(CONCAT('%', :query, '%'))) " +
           "ORDER BY t.createdAt DESC")
    List<Ticket> search(@Param("query") String query);

    // ==================== CONTADORES Y ESTADÍSTICAS ====================

    /**
     * Cuenta todos los tickets no eliminados.
     */
    Long countByDeletedAtIsNull();

    /**
     * Cuenta tickets activos (no entregados ni cancelados).
     */
    @Query("SELECT COUNT(t) FROM Ticket t WHERE t.deletedAt IS NULL AND " +
           "t.estado NOT IN (com.tesig.model.EstadoTicket.ENTREGADO, com.tesig.model.EstadoTicket.CANCELADO)")
    Long countActivos();

    /**
     * Cuenta tickets por estado.
     */
    Long countByEstadoAndDeletedAtIsNull(EstadoTicket estado);

    /**
     * Cuenta tickets agrupados por técnico.
     * Retorna una lista de arrays donde [0] = nombreCompleto del técnico, [1] = cantidad de tickets
     */
    @Query("SELECT CONCAT(t.tecnicoAsignado.nombre, ' ', t.tecnicoAsignado.apellido), COUNT(t) " +
           "FROM Ticket t " +
           "WHERE t.deletedAt IS NULL AND t.tecnicoAsignado IS NOT NULL " +
           "GROUP BY t.tecnicoAsignado.id, t.tecnicoAsignado.nombre, t.tecnicoAsignado.apellido " +
           "ORDER BY COUNT(t) DESC")
    List<Object[]> countTicketsPorTecnico();

    /**
     * Calcula el tiempo promedio de reparación en días.
     * Solo considera tickets entregados que tienen fecha de creación y entrega.
     */
    @Query("SELECT AVG(CAST((EXTRACT(EPOCH FROM t.fechaEntrega) - EXTRACT(EPOCH FROM t.createdAt)) / 86400 AS double)) " +
           "FROM Ticket t " +
           "WHERE t.deletedAt IS NULL " +
           "AND t.estado = com.tesig.model.EstadoTicket.ENTREGADO " +
           "AND t.createdAt IS NOT NULL " +
           "AND t.fechaEntrega IS NOT NULL")
    Double calcularTiempoPromedioReparacion();

    // ==================== VALIDACIONES DE NEGOCIO ====================

    /**
     * Verifica si un cliente tiene tickets activos (no entregados ni cancelados).
     * Útil para validar antes de eliminar un cliente.
     */
    @Query("SELECT CASE WHEN COUNT(t) > 0 THEN true ELSE false END " +
           "FROM Ticket t " +
           "WHERE t.cliente.id = :clienteId " +
           "AND t.deletedAt IS NULL " +
           "AND t.estado NOT IN (com.tesig.model.EstadoTicket.ENTREGADO, com.tesig.model.EstadoTicket.CANCELADO)")
    boolean existsActivosByClienteId(@Param("clienteId") Long clienteId);
}
