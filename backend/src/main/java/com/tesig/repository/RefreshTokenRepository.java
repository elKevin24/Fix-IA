package com.tesig.repository;

import com.tesig.model.RefreshToken;
import com.tesig.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByToken(String token);

    @Query("SELECT rt FROM RefreshToken rt WHERE rt.usuario.id = :usuarioId AND rt.activo = true AND rt.deletedAt IS NULL")
    List<RefreshToken> findActiveTokensByUsuarioId(@Param("usuarioId") Long usuarioId);

    @Modifying
    @Query("UPDATE RefreshToken rt SET rt.activo = false WHERE rt.usuario.id = :usuarioId")
    void invalidateAllUserTokens(@Param("usuarioId") Long usuarioId);

    @Modifying
    @Query("UPDATE RefreshToken rt SET rt.activo = false WHERE rt.fechaExpiracion < :now")
    void invalidateExpiredTokens(@Param("now") LocalDateTime now);

    @Query("SELECT COUNT(rt) FROM RefreshToken rt WHERE rt.usuario = :usuario AND rt.activo = true AND rt.deletedAt IS NULL")
    Long countActiveTokensByUsuario(@Param("usuario") Usuario usuario);
}
