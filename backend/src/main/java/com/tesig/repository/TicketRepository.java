package com.tesig.repository;

import com.tesig.model.EstadoTicket;
import com.tesig.model.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {

    // Búsqueda por número de ticket (para consulta pública)
    Optional<Ticket> findByNumeroTicket(String numeroTicket);

    // Búsqueda de tickets por cliente
    @Query("SELECT t FROM Ticket t WHERE t.cliente.id = :clienteId AND t.deletedAt IS NULL ORDER BY t.createdAt DESC")
    List<Ticket> findByClienteId(@Param("clienteId") Long clienteId);

    // Búsqueda de tickets por técnico asignado
    @Query("SELECT t FROM Ticket t WHERE t.tecnicoAsignado.id = :tecnicoId AND t.deletedAt IS NULL ORDER BY t.createdAt DESC")
    List<Ticket> findByTecnicoAsignadoId(@Param("tecnicoId") Long tecnicoId);

    // Búsqueda de tickets por estado
    @Query("SELECT t FROM Ticket t WHERE t.estado = :estado AND t.deletedAt IS NULL ORDER BY t.createdAt DESC")
    List<Ticket> findByEstado(@Param("estado") EstadoTicket estado);

    // Búsqueda de tickets activos (no entregados ni cancelados)
    @Query("SELECT t FROM Ticket t WHERE t.deletedAt IS NULL AND " +
           "t.estado NOT IN ('ENTREGADO', 'CANCELADO') " +
           "ORDER BY t.createdAt DESC")
    List<Ticket> findTicketsActivos();

    // Búsqueda de tickets por rango de fechas
    @Query("SELECT t FROM Ticket t WHERE t.deletedAt IS NULL AND " +
           "t.createdAt BETWEEN :fechaInicio AND :fechaFin " +
           "ORDER BY t.createdAt DESC")
    List<Ticket> findByFechaCreacionBetween(
        @Param("fechaInicio") LocalDateTime fechaInicio,
        @Param("fechaFin") LocalDateTime fechaFin
    );

    // Contar tickets por estado
    @Query("SELECT COUNT(t) FROM Ticket t WHERE t.estado = :estado AND t.deletedAt IS NULL")
    Long countByEstado(@Param("estado") EstadoTicket estado);

    // Verificar si existe un número de ticket
    boolean existsByNumeroTicket(String numeroTicket);
}
