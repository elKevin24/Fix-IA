package com.tesig.repository;

import com.tesig.model.Rol;
import com.tesig.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByEmail(String email);

    @Query("SELECT u FROM Usuario u WHERE u.deletedAt IS NULL AND u.activo = true")
    List<Usuario> findAllActivos();

    @Query("SELECT u FROM Usuario u WHERE u.deletedAt IS NULL AND u.activo = true AND u.rol = :rol")
    List<Usuario> findByRolAndActivo(Rol rol);

    boolean existsByEmail(String email);
}
