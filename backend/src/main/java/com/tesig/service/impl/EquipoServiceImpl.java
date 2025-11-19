package com.tesig.service.impl;

import com.tesig.dto.equipo.ActualizarEquipoDTO;
import com.tesig.dto.equipo.CrearEquipoDTO;
import com.tesig.dto.equipo.EquipoResponseDTO;
import com.tesig.exception.ResourceNotFoundException;
import com.tesig.model.Equipo;
import com.tesig.model.Ticket;
import com.tesig.repository.EquipoRepository;
import com.tesig.repository.TicketRepository;
import com.tesig.service.IEquipoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementación del servicio de gestión de equipos.
 * Permite manejar múltiples equipos por ticket.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class EquipoServiceImpl implements IEquipoService {

    private final EquipoRepository equipoRepository;
    private final TicketRepository ticketRepository;

    @Override
    public EquipoResponseDTO agregarEquipo(Long ticketId, CrearEquipoDTO dto) {
        log.info("Agregando equipo al ticket: {}", ticketId);

        Ticket ticket = ticketRepository.findByIdAndDeletedAtIsNull(ticketId)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket no encontrado: " + ticketId));

        Equipo equipo = new Equipo();
        equipo.setTicket(ticket);
        equipo.setTipoEquipo(dto.getTipoEquipo());
        equipo.setMarca(dto.getMarca());
        equipo.setModelo(dto.getModelo());
        equipo.setNumeroSerie(dto.getNumeroSerie());
        equipo.setAccesorios(dto.getAccesorios());
        equipo.setProblemaReportado(dto.getProblemaReportado());
        equipo.setDeleted(false);

        Equipo saved = equipoRepository.save(equipo);
        log.info("Equipo agregado exitosamente: {} - {}", saved.getId(), saved.getTipoEquipo());

        return toDTO(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EquipoResponseDTO> obtenerEquiposDeTicket(Long ticketId) {
        log.debug("Obteniendo equipos del ticket: {}", ticketId);

        // Verificar que el ticket existe
        if (!ticketRepository.existsById(ticketId)) {
            throw new ResourceNotFoundException("Ticket no encontrado: " + ticketId);
        }

        List<Equipo> equipos = equipoRepository.findByTicketId(ticketId);
        return equipos.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public EquipoResponseDTO obtenerEquipoPorId(Long equipoId) {
        log.debug("Obteniendo equipo: {}", equipoId);

        Equipo equipo = equipoRepository.findByIdAndNotDeleted(equipoId)
                .orElseThrow(() -> new ResourceNotFoundException("Equipo no encontrado: " + equipoId));

        return toDTO(equipo);
    }

    @Override
    public EquipoResponseDTO actualizarEquipo(Long equipoId, ActualizarEquipoDTO dto) {
        log.info("Actualizando equipo: {}", equipoId);

        Equipo equipo = equipoRepository.findByIdAndNotDeleted(equipoId)
                .orElseThrow(() -> new ResourceNotFoundException("Equipo no encontrado: " + equipoId));

        if (dto.getTipoEquipo() != null) {
            equipo.setTipoEquipo(dto.getTipoEquipo());
        }
        if (dto.getMarca() != null) {
            equipo.setMarca(dto.getMarca());
        }
        if (dto.getModelo() != null) {
            equipo.setModelo(dto.getModelo());
        }
        if (dto.getNumeroSerie() != null) {
            equipo.setNumeroSerie(dto.getNumeroSerie());
        }
        if (dto.getAccesorios() != null) {
            equipo.setAccesorios(dto.getAccesorios());
        }
        if (dto.getProblemaReportado() != null) {
            equipo.setProblemaReportado(dto.getProblemaReportado());
        }
        if (dto.getDiagnostico() != null) {
            equipo.setDiagnostico(dto.getDiagnostico());
        }
        if (dto.getTrabajoRealizado() != null) {
            equipo.setTrabajoRealizado(dto.getTrabajoRealizado());
        }

        Equipo saved = equipoRepository.save(equipo);
        log.info("Equipo actualizado: {}", equipoId);

        return toDTO(saved);
    }

    @Override
    public EquipoResponseDTO actualizarDiagnostico(Long equipoId, String diagnostico) {
        log.info("Actualizando diagnóstico del equipo: {}", equipoId);

        Equipo equipo = equipoRepository.findByIdAndNotDeleted(equipoId)
                .orElseThrow(() -> new ResourceNotFoundException("Equipo no encontrado: " + equipoId));

        equipo.setDiagnostico(diagnostico);
        Equipo saved = equipoRepository.save(equipo);

        return toDTO(saved);
    }

    @Override
    public EquipoResponseDTO actualizarTrabajoRealizado(Long equipoId, String trabajoRealizado) {
        log.info("Actualizando trabajo realizado del equipo: {}", equipoId);

        Equipo equipo = equipoRepository.findByIdAndNotDeleted(equipoId)
                .orElseThrow(() -> new ResourceNotFoundException("Equipo no encontrado: " + equipoId));

        equipo.setTrabajoRealizado(trabajoRealizado);
        Equipo saved = equipoRepository.save(equipo);

        return toDTO(saved);
    }

    @Override
    public void eliminarEquipo(Long equipoId) {
        log.info("Eliminando equipo: {}", equipoId);

        Equipo equipo = equipoRepository.findByIdAndNotDeleted(equipoId)
                .orElseThrow(() -> new ResourceNotFoundException("Equipo no encontrado: " + equipoId));

        equipo.setDeleted(true);
        equipoRepository.save(equipo);

        log.info("Equipo eliminado: {}", equipoId);
    }

    @Override
    @Transactional(readOnly = true)
    public int contarEquiposEnTicket(Long ticketId) {
        return equipoRepository.findByTicketId(ticketId).size();
    }

    /**
     * Convierte entidad a DTO
     */
    private EquipoResponseDTO toDTO(Equipo equipo) {
        EquipoResponseDTO dto = new EquipoResponseDTO();
        dto.setId(equipo.getId());
        dto.setTicketId(equipo.getTicket().getId());
        dto.setTipoEquipo(equipo.getTipoEquipo());
        dto.setMarca(equipo.getMarca());
        dto.setModelo(equipo.getModelo());
        dto.setNumeroSerie(equipo.getNumeroSerie());
        dto.setAccesorios(equipo.getAccesorios());
        dto.setProblemaReportado(equipo.getProblemaReportado());
        dto.setDiagnostico(equipo.getDiagnostico());
        dto.setTrabajoRealizado(equipo.getTrabajoRealizado());
        dto.setCreatedAt(equipo.getCreatedAt());
        return dto;
    }
}
