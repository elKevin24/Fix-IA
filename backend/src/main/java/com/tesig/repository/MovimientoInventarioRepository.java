package com.tesig.repository;

import com.tesig.model.MovimientoInventario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MovimientoInventarioRepository extends JpaRepository<MovimientoInventario, Long> {

    /**
     * Encuentra todos los movimientos de una pieza
     */
    @Query("SELECT m FROM MovimientoInventario m WHERE m.pieza.id = :piezaId ORDER BY m.createdAt DESC")
    Page<MovimientoInventario> findByPiezaId(@Param("piezaId") Long piezaId, Pageable pageable);

    /**
     * Encuentra movimientos por tipo
     */
    @Query("SELECT m FROM MovimientoInventario m WHERE m.tipoMovimiento = :tipo ORDER BY m.createdAt DESC")
    Page<MovimientoInventario> findByTipoMovimiento(@Param("tipo") MovimientoInventario.TipoMovimiento tipo, Pageable pageable);

    /**
     * Encuentra movimientos de una compra
     */
    @Query("SELECT m FROM MovimientoInventario m WHERE m.compra.id = :compraId ORDER BY m.createdAt DESC")
    List<MovimientoInventario> findByCompraId(@Param("compraId") Long compraId);

    /**
     * Encuentra movimientos de un ticket
     */
    @Query("SELECT m FROM MovimientoInventario m WHERE m.ticket.id = :ticketId ORDER BY m.createdAt DESC")
    List<MovimientoInventario> findByTicketId(@Param("ticketId") Long ticketId);

    /**
     * Encuentra movimientos por rango de fechas
     */
    @Query("SELECT m FROM MovimientoInventario m WHERE m.createdAt BETWEEN :fechaInicio AND :fechaFin ORDER BY m.createdAt DESC")
    List<MovimientoInventario> findByCreatedAtBetween(@Param("fechaInicio") LocalDateTime fechaInicio, @Param("fechaFin") LocalDateTime fechaFin);

    /**
     * Encuentra últimos movimientos
     */
    @Query("SELECT m FROM MovimientoInventario m ORDER BY m.createdAt DESC")
    Page<MovimientoInventario> findAllOrderByCreatedAtDesc(Pageable pageable);
}
