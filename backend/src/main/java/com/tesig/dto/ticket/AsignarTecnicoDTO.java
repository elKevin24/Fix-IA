package com.tesig.dto.ticket;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para asignar un técnico a un ticket.
 * Single Responsibility: Solo asignación de técnico.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AsignarTecnicoDTO {

    @NotNull(message = "El ID del técnico es obligatorio")
    private Long tecnicoId;
}
