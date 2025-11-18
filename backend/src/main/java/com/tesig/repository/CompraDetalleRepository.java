package com.tesig.repository;

import com.tesig.model.CompraDetalle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CompraDetalleRepository extends JpaRepository<CompraDetalle, Long> {

    /**
     * Encuentra todos los detalles de una compra
     */
    @Query("SELECT cd FROM CompraDetalle cd WHERE cd.compra.id = :compraId")
    List<CompraDetalle> findByCompraId(@Param("compraId") Long compraId);

    /**
     * Encuentra detalles por pieza
     */
    @Query("SELECT cd FROM CompraDetalle cd WHERE cd.pieza.id = :piezaId")
    List<CompraDetalle> findByPiezaId(@Param("piezaId") Long piezaId);
}
