package com.tesig.service;

import com.tesig.dto.equipo.ActualizarEquipoDTO;
import com.tesig.dto.equipo.CrearEquipoDTO;
import com.tesig.dto.equipo.EquipoResponseDTO;

import java.util.List;

/**
 * Interface para el servicio de gestión de equipos.
 * Permite manejar múltiples equipos por ticket.
 */
public interface IEquipoService {

    /**
     * Agrega un equipo a un ticket existente.
     */
    EquipoResponseDTO agregarEquipo(Long ticketId, CrearEquipoDTO dto);

    /**
     * Obtiene todos los equipos de un ticket.
     */
    List<EquipoResponseDTO> obtenerEquiposDeTicket(Long ticketId);

    /**
     * Obtiene un equipo por su ID.
     */
    EquipoResponseDTO obtenerEquipoPorId(Long equipoId);

    /**
     * Actualiza la información de un equipo.
     */
    EquipoResponseDTO actualizarEquipo(Long equipoId, ActualizarEquipoDTO dto);

    /**
     * Actualiza el diagnóstico de un equipo específico.
     */
    EquipoResponseDTO actualizarDiagnostico(Long equipoId, String diagnostico);

    /**
     * Actualiza el trabajo realizado en un equipo específico.
     */
    EquipoResponseDTO actualizarTrabajoRealizado(Long equipoId, String trabajoRealizado);

    /**
     * Elimina un equipo de un ticket.
     */
    void eliminarEquipo(Long equipoId);

    /**
     * Cuenta el número de equipos en un ticket.
     */
    int contarEquiposEnTicket(Long ticketId);
}
