package com.tesig.repository;

import com.tesig.model.Pieza;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio para la entidad Pieza.
 *
 * Aplicación de principios:
 * - Dependency Inversion: Define abstracción para acceso a datos
 * - Interface Segregation: Solo métodos necesarios para Pieza
 *
 * @author TESIG System
 */
@Repository
public interface PiezaRepository extends JpaRepository<Pieza, Long> {

    /**
     * Busca una pieza por su código único (SKU)
     */
    Optional<Pieza> findByCodigoAndDeletedAtIsNull(String codigo);

    /**
     * Busca piezas activas por nombre (búsqueda parcial)
     */
    @Query("SELECT p FROM Pieza p WHERE p.deletedAt IS NULL " +
           "AND LOWER(p.nombre) LIKE LOWER(CONCAT('%', :nombre, '%'))")
    Page<Pieza> findByNombreContainingIgnoreCase(@Param("nombre") String nombre, Pageable pageable);

    /**
     * Busca piezas por categoría
     */
    @Query("SELECT p FROM Pieza p WHERE p.deletedAt IS NULL " +
           "AND p.categoria = :categoria")
    Page<Pieza> findByCategoria(@Param("categoria") String categoria, Pageable pageable);

    /**
     * Busca piezas por marca
     */
    @Query("SELECT p FROM Pieza p WHERE p.deletedAt IS NULL " +
           "AND LOWER(p.marca) LIKE LOWER(CONCAT('%', :marca, '%'))")
    Page<Pieza> findByMarcaContainingIgnoreCase(@Param("marca") String marca, Pageable pageable);

    /**
     * Busca todas las piezas activas
     */
    @Query("SELECT p FROM Pieza p WHERE p.deletedAt IS NULL")
    Page<Pieza> findAllActive(Pageable pageable);

    /**
     * Busca piezas que necesitan reabastecimiento (stock <= stockMinimo)
     */
    @Query("SELECT p FROM Pieza p WHERE p.deletedAt IS NULL " +
           "AND p.stock <= p.stockMinimo " +
           "ORDER BY p.stock ASC")
    List<Pieza> findPiezasConStockBajo();

    /**
     * Busca piezas sin stock
     */
    @Query("SELECT p FROM Pieza p WHERE p.deletedAt IS NULL " +
           "AND p.stock = 0")
    List<Pieza> findPiezasSinStock();

    /**
     * Busca piezas activas con stock disponible
     */
    @Query("SELECT p FROM Pieza p WHERE p.deletedAt IS NULL " +
           "AND p.activo = true " +
           "AND p.stock > 0")
    Page<Pieza> findPiezasDisponibles(Pageable pageable);

    /**
     * Búsqueda global por múltiples criterios
     */
    @Query("SELECT p FROM Pieza p WHERE p.deletedAt IS NULL " +
           "AND (LOWER(p.codigo) LIKE LOWER(CONCAT('%', :busqueda, '%')) " +
           "OR LOWER(p.nombre) LIKE LOWER(CONCAT('%', :busqueda, '%')) " +
           "OR LOWER(p.descripcion) LIKE LOWER(CONCAT('%', :busqueda, '%')) " +
           "OR LOWER(p.marca) LIKE LOWER(CONCAT('%', :busqueda, '%')) " +
           "OR LOWER(p.modelo) LIKE LOWER(CONCAT('%', :busqueda, '%')))")
    Page<Pieza> buscarGlobal(@Param("busqueda") String busqueda, Pageable pageable);

    /**
     * Obtiene todas las categorías distintas
     */
    @Query("SELECT DISTINCT p.categoria FROM Pieza p WHERE p.deletedAt IS NULL ORDER BY p.categoria")
    List<String> findAllCategorias();

    /**
     * Busca pieza por ID excluyendo eliminadas
     */
    Optional<Pieza> findByIdAndDeletedAtIsNull(Long id);

    /**
     * Verifica si existe una pieza con el código dado (excluyendo una pieza específica)
     */
    @Query("SELECT COUNT(p) > 0 FROM Pieza p WHERE p.codigo = :codigo " +
           "AND p.id != :id " +
           "AND p.deletedAt IS NULL")
    boolean existsByCodigoAndIdNot(@Param("codigo") String codigo, @Param("id") Long id);

    /**
     * Verifica si existe una pieza con el código dado
     */
    boolean existsByCodigoAndDeletedAtIsNull(String codigo);
}
