package com.tesig.dto.ticket;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para registrar la entrega del equipo.
 * Single Responsibility: Solo información de entrega.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EntregaDTO {

    @Size(max = 1000, message = "Las observaciones no pueden exceder 1000 caracteres")
    private String observacionesEntrega;
}
