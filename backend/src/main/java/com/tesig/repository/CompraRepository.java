package com.tesig.repository;

import com.tesig.model.Compra;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface CompraRepository extends JpaRepository<Compra, Long> {

    /**
     * Busca una compra por código
     */
    @Query("SELECT c FROM Compra c WHERE c.codigoCompra = :codigo AND c.deleted = false")
    Optional<Compra> findByCodigoCompra(@Param("codigo") String codigo);

    /**
     * Busca una compra por ID que no esté eliminada
     */
    @Query("SELECT c FROM Compra c WHERE c.id = :id AND c.deleted = false")
    Optional<Compra> findByIdAndNotDeleted(@Param("id") Long id);

    /**
     * Encuentra todas las compras no eliminadas con paginación
     */
    @Query("SELECT c FROM Compra c WHERE c.deleted = false ORDER BY c.fechaCompra DESC")
    Page<Compra> findAllNotDeleted(Pageable pageable);

    /**
     * Busca compras por proveedor
     */
    @Query("SELECT c FROM Compra c WHERE LOWER(c.proveedor) LIKE LOWER(CONCAT('%', :proveedor, '%')) AND c.deleted = false")
    Page<Compra> findByProveedorContaining(@Param("proveedor") String proveedor, Pageable pageable);

    /**
     * Busca compras por estado
     */
    @Query("SELECT c FROM Compra c WHERE c.estado = :estado AND c.deleted = false ORDER BY c.fechaCompra DESC")
    Page<Compra> findByEstado(@Param("estado") Compra.EstadoCompra estado, Pageable pageable);

    /**
     * Busca compras por rango de fechas
     */
    @Query("SELECT c FROM Compra c WHERE c.fechaCompra BETWEEN :fechaInicio AND :fechaFin AND c.deleted = false ORDER BY c.fechaCompra DESC")
    List<Compra> findByFechaCompraBetween(@Param("fechaInicio") LocalDate fechaInicio, @Param("fechaFin") LocalDate fechaFin);

    /**
     * Calcula el total de compras en un rango de fechas
     */
    @Query("SELECT COALESCE(SUM(c.total), 0) FROM Compra c WHERE c.fechaCompra BETWEEN :fechaInicio AND :fechaFin AND c.estado = :estado AND c.deleted = false")
    BigDecimal sumTotalByFechaAndEstado(@Param("fechaInicio") LocalDate fechaInicio, @Param("fechaFin") LocalDate fechaFin, @Param("estado") Compra.EstadoCompra estado);

    /**
     * Encuentra compras pendientes de recepción
     */
    @Query("SELECT c FROM Compra c WHERE c.estado = 'PENDIENTE' AND c.deleted = false ORDER BY c.fechaCompra ASC")
    List<Compra> findComprasPendientes();
}
