package com.tesig.dto.ticket;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para rechazar un presupuesto.
 * Single Responsibility: Solo rechazo con motivo.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RechazarPresupuestoDTO {

    @NotBlank(message = "El motivo de rechazo es obligatorio")
    @Size(max = 500, message = "El motivo no puede exceder 500 caracteres")
    private String motivoRechazo;
}
