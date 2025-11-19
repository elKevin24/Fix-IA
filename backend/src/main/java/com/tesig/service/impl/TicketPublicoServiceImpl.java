package com.tesig.service.impl;

import com.tesig.dto.TicketConsultaPublicaDTO;
import com.tesig.exception.ResourceNotFoundException;
import com.tesig.mapper.TicketMapper;
import com.tesig.model.Ticket;
import com.tesig.repository.TicketRepository;
import com.tesig.service.ITicketPublicoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementación del servicio de consulta pública de tickets.
 *
 * Principios SOLID aplicados:
 * - Single Responsibility: Solo implementa lógica de consultas públicas
 * - Open/Closed: Abierto a extensión mediante la interface, cerrado a modificación
 * - Liskov Substitution: Puede ser sustituida por cualquier implementación de la interface
 * - Dependency Inversion: Depende de abstracciones (Repository, Mapper)
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class TicketPublicoServiceImpl implements ITicketPublicoService {

    private final TicketRepository ticketRepository;
    private final TicketMapper ticketMapper;

    @Override
    public TicketConsultaPublicaDTO consultarTicket(String numeroTicket) {
        log.info("Consulta pública del ticket: {}", numeroTicket);

        // Normalizar el número de ticket (quitar espacios, convertir a mayúsculas)
        String numeroNormalizado = normalizarNumeroTicket(numeroTicket);

        Ticket ticket = buscarTicketPorNumero(numeroNormalizado);
        validarTicketNoEliminado(ticket, numeroNormalizado);

        log.info("Ticket encontrado: {} - Estado: {}", numeroNormalizado, ticket.getEstado());

        return ticketMapper.toConsultaPublicaDTO(ticket);
    }

    @Override
    public boolean existeTicket(String numeroTicket) {
        String numeroNormalizado = normalizarNumeroTicket(numeroTicket);
        boolean existe = ticketRepository.existsByNumeroTicket(numeroNormalizado);

        log.debug("Verificación de existencia del ticket {}: {}", numeroNormalizado, existe);

        return existe;
    }

    // Métodos privados helpers - Single Responsibility

    private String normalizarNumeroTicket(String numeroTicket) {
        return numeroTicket.trim().toUpperCase();
    }

    private Ticket buscarTicketPorNumero(String numeroNormalizado) {
        return ticketRepository.findByNumeroTicket(numeroNormalizado)
                .orElseThrow(() -> {
                    log.warn("Ticket no encontrado: {}", numeroNormalizado);
                    return new ResourceNotFoundException("Ticket", "número", numeroNormalizado);
                });
    }

    private void validarTicketNoEliminado(Ticket ticket, String numeroNormalizado) {
        if (ticket.isDeleted()) {
            log.warn("Intento de acceso a ticket eliminado: {}", numeroNormalizado);
            throw new ResourceNotFoundException("Ticket", "número", numeroNormalizado);
        }
    }
}
