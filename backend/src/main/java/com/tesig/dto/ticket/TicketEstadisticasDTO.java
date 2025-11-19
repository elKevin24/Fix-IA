package com.tesig.dto.ticket;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * DTO para estadísticas de tickets.
 * Open/Closed Principle: Fácil de extender sin modificar.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TicketEstadisticasDTO {

    private Long totalTickets;
    private Long ticketsActivos;
    private Map<String, Long> ticketsPorEstado;
    private Map<String, Long> ticketsPorTecnico;
    private Double tiempoPromedioReparacion; // en días
}
