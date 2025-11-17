package com.tesig.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO para respuesta de pieza asociada a un ticket.
 * Contiene la información de la pieza en el contexto del ticket.
 *
 * Aplicación de principios:
 * - Single Responsibility: Solo para transferir datos de pieza de ticket
 * - Information Hiding: Expone solo lo necesario al cliente
 *
 * @author TESIG System
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TicketPiezaResponseDTO {

    private Long id;
    private Long ticketId;

    // Información de la pieza
    private Long piezaId;
    private String piezaCodigo;
    private String piezaNombre;
    private String piezaDescripcion;
    private String piezaMarca;
    private String piezaModelo;

    // Información del uso en el ticket
    private Integer cantidad;
    private BigDecimal precioUnitario;
    private BigDecimal subtotal;
    private Boolean stockDescontado;
    private String notas;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /**
     * Formatea el subtotal
     */
    public String getSubtotalFormateado() {
        if (subtotal == null) {
            return "$0.00";
        }
        return String.format("$%.2f", subtotal);
    }

    /**
     * Formatea el precio unitario
     */
    public String getPrecioUnitarioFormateado() {
        if (precioUnitario == null) {
            return "$0.00";
        }
        return String.format("$%.2f", precioUnitario);
    }
}
