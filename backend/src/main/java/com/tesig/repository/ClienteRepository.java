package com.tesig.repository;

import com.tesig.model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {

    Optional<Cliente> findByTelefono(String telefono);

    Optional<Cliente> findByEmail(String email);

    @Query("SELECT c FROM Cliente c WHERE c.deletedAt IS NULL AND " +
           "(LOWER(c.nombre) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(c.apellido) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "c.telefono LIKE CONCAT('%', :search, '%'))")
    List<Cliente> buscarClientes(@Param("search") String search);

    @Query("SELECT c FROM Cliente c WHERE c.deletedAt IS NULL")
    List<Cliente> findAllActivos();
}
