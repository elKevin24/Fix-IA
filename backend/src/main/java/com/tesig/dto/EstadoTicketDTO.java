package com.tesig.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para representar el estado de un ticket.
 * MapStruct se encarga del mapeo desde el enum.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EstadoTicketDTO {
    private String codigo;
    private String nombre;
    private String descripcion;
}
