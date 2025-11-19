package com.tesig.repository;

import com.tesig.model.Gasto;
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
public interface GastoRepository extends JpaRepository<Gasto, Long> {

    /**
     * Busca un gasto por ID que no esté eliminado
     */
    @Query("SELECT g FROM Gasto g WHERE g.id = :id AND g.deleted = false")
    Optional<Gasto> findByIdAndNotDeleted(@Param("id") Long id);

    /**
     * Encuentra todos los gastos no eliminados con paginación
     */
    @Query("SELECT g FROM Gasto g WHERE g.deleted = false ORDER BY g.fecha DESC")
    Page<Gasto> findAllNotDeleted(Pageable pageable);

    /**
     * Busca gastos por categoría
     */
    @Query("SELECT g FROM Gasto g WHERE g.categoria = :categoria AND g.deleted = false ORDER BY g.fecha DESC")
    Page<Gasto> findByCategoria(@Param("categoria") Gasto.CategoriaGasto categoria, Pageable pageable);

    /**
     * Busca gastos por rango de fechas
     */
    @Query("SELECT g FROM Gasto g WHERE g.fecha BETWEEN :fechaInicio AND :fechaFin AND g.deleted = false ORDER BY g.fecha DESC")
    List<Gasto> findByFechaBetween(@Param("fechaInicio") LocalDate fechaInicio, @Param("fechaFin") LocalDate fechaFin);

    /**
     * Calcula el total de gastos en un rango de fechas
     */
    @Query("SELECT COALESCE(SUM(g.monto), 0) FROM Gasto g WHERE g.fecha BETWEEN :fechaInicio AND :fechaFin AND g.deleted = false")
    BigDecimal sumMontoByFechaBetween(@Param("fechaInicio") LocalDate fechaInicio, @Param("fechaFin") LocalDate fechaFin);

    /**
     * Calcula el total de gastos por categoría en un rango de fechas
     */
    @Query("SELECT COALESCE(SUM(g.monto), 0) FROM Gasto g WHERE g.fecha BETWEEN :fechaInicio AND :fechaFin AND g.categoria = :categoria AND g.deleted = false")
    BigDecimal sumMontoByFechaAndCategoria(@Param("fechaInicio") LocalDate fechaInicio, @Param("fechaFin") LocalDate fechaFin, @Param("categoria") Gasto.CategoriaGasto categoria);

    /**
     * Busca gastos por proveedor
     */
    @Query("SELECT g FROM Gasto g WHERE LOWER(g.proveedor) LIKE LOWER(CONCAT('%', :proveedor, '%')) AND g.deleted = false ORDER BY g.fecha DESC")
    Page<Gasto> findByProveedorContaining(@Param("proveedor") String proveedor, Pageable pageable);

    /**
     * Busca gastos por concepto
     */
    @Query("SELECT g FROM Gasto g WHERE LOWER(g.concepto) LIKE LOWER(CONCAT('%', :concepto, '%')) AND g.deleted = false ORDER BY g.fecha DESC")
    Page<Gasto> findByConceptoContaining(@Param("concepto") String concepto, Pageable pageable);
}
