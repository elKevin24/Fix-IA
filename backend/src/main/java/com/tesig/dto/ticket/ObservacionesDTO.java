package com.tesig.dto.ticket;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para agregar observaciones durante la reparación.
 * Single Responsibility: Solo observaciones de reparación.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ObservacionesDTO {

    @Size(max = 2000, message = "Las observaciones no pueden exceder 2000 caracteres")
    private String observaciones;
}
