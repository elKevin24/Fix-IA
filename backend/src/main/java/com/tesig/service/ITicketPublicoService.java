package com.tesig.service;

import com.tesig.dto.TicketConsultaPublicaDTO;

/**
 * Interface para servicios de consulta pública de tickets.
 *
 * Principios SOLID aplicados:
 * - Single Responsibility: Solo maneja consultas públicas de tickets
 * - Interface Segregation: Interface específica para operaciones públicas
 * - Dependency Inversion: Los clientes dependen de esta abstracción
 */
public interface ITicketPublicoService {

    /**
     * Consulta un ticket por su número sin requerir autenticación.
     * Solo retorna información pública del ticket.
     *
     * @param numeroTicket Número único del ticket
     * @return Información pública del ticket
     * @throws com.tesig.exception.ResourceNotFoundException si el ticket no existe
     */
    TicketConsultaPublicaDTO consultarTicket(String numeroTicket);

    /**
     * Valida si un número de ticket existe en el sistema.
     *
     * @param numeroTicket Número del ticket a validar
     * @return true si existe, false si no existe
     */
    boolean existeTicket(String numeroTicket);
}
