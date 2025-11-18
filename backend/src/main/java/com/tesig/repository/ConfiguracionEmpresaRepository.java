package com.tesig.repository;

import com.tesig.model.ConfiguracionEmpresa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repositorio para la configuración de empresa.
 *
 * @author TESIG System
 */
@Repository
public interface ConfiguracionEmpresaRepository extends JpaRepository<ConfiguracionEmpresa, Long> {

    /**
     * Obtiene la configuración activa de la empresa
     */
    Optional<ConfiguracionEmpresa> findByActivoTrue();

    /**
     * Obtiene configuración por código de empresa
     */
    Optional<ConfiguracionEmpresa> findByCodigoEmpresa(String codigoEmpresa);

    /**
     * Obtiene configuración por código de empresa y sucursal
     */
    Optional<ConfiguracionEmpresa> findByCodigoEmpresaAndCodigoSucursal(String codigoEmpresa, String codigoSucursal);

    /**
     * Verifica si existe una configuración activa
     */
    boolean existsByActivoTrue();

    /**
     * Obtiene la primera configuración activa (para sistemas con una sola sucursal)
     */
    @Query("SELECT c FROM ConfiguracionEmpresa c WHERE c.activo = true ORDER BY c.id ASC LIMIT 1")
    Optional<ConfiguracionEmpresa> findFirstActiveConfiguration();
}
