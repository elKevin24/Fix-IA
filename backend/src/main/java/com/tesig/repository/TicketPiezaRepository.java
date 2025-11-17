package com.tesig.repository;

import com.tesig.model.TicketPieza;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

/**
 * Repositorio para la entidad TicketPieza.
 *
 * Aplicación de principios:
 * - Dependency Inversion: Define abstracción para acceso a datos
 * - Interface Segregation: Solo métodos necesarios para TicketPieza
 *
 * @author TESIG System
 */
@Repository
public interface TicketPiezaRepository extends JpaRepository<TicketPieza, Long> {

    /**
     * Busca todas las piezas asociadas a un ticket
     */
    @Query("SELECT tp FROM TicketPieza tp WHERE tp.ticket.id = :ticketId")
    List<TicketPieza> findByTicketId(@Param("ticketId") Long ticketId);

    /**
     * Busca todos los tickets que utilizaron una pieza específica
     */
    @Query("SELECT tp FROM TicketPieza tp WHERE tp.pieza.id = :piezaId")
    List<TicketPieza> findByPiezaId(@Param("piezaId") Long piezaId);

    /**
     * Calcula el total de piezas de un ticket
     */
    @Query("SELECT COALESCE(SUM(tp.subtotal), 0) FROM TicketPieza tp WHERE tp.ticket.id = :ticketId")
    BigDecimal calcularTotalPiezasPorTicket(@Param("ticketId") Long ticketId);

    /**
     * Obtiene las piezas más utilizadas en reparaciones
     */
    @Query("SELECT tp.pieza.id, tp.pieza.nombre, SUM(tp.cantidad) as totalUsado " +
           "FROM TicketPieza tp " +
           "WHERE tp.stockDescontado = true " +
           "GROUP BY tp.pieza.id, tp.pieza.nombre " +
           "ORDER BY totalUsado DESC")
    List<Object[]> findPiezasMasUtilizadas();

    /**
     * Busca piezas de un ticket que aún no han sido descontadas del inventario
     */
    @Query("SELECT tp FROM TicketPieza tp " +
           "WHERE tp.ticket.id = :ticketId " +
           "AND tp.stockDescontado = false")
    List<TicketPieza> findPiezasPendientesDeDescuento(@Param("ticketId") Long ticketId);

    /**
     * Busca piezas de un ticket que ya fueron descontadas del inventario
     */
    @Query("SELECT tp FROM TicketPieza tp " +
           "WHERE tp.ticket.id = :ticketId " +
           "AND tp.stockDescontado = true")
    List<TicketPieza> findPiezasDescontadas(@Param("ticketId") Long ticketId);

    /**
     * Elimina todas las piezas asociadas a un ticket
     */
    void deleteByTicketId(Long ticketId);
}
