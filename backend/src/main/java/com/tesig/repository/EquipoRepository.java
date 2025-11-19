package com.tesig.repository;

import com.tesig.model.Equipo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EquipoRepository extends JpaRepository<Equipo, Long> {

    /**
     * Encuentra todos los equipos de un ticket
     */
    @Query("SELECT e FROM Equipo e WHERE e.ticket.id = :ticketId AND e.deleted = false")
    List<Equipo> findByTicketId(@Param("ticketId") Long ticketId);

    /**
     * Busca un equipo por ID que no esté eliminado
     */
    @Query("SELECT e FROM Equipo e WHERE e.id = :id AND e.deleted = false")
    Optional<Equipo> findByIdAndNotDeleted(@Param("id") Long id);

    /**
     * Busca equipos por tipo
     */
    @Query("SELECT e FROM Equipo e WHERE LOWER(e.tipoEquipo) LIKE LOWER(CONCAT('%', :tipo, '%')) AND e.deleted = false")
    List<Equipo> findByTipoEquipoContaining(@Param("tipo") String tipo);
}
