package com.tesig.dto.ticket;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para crear un nuevo ticket (ingreso de equipo).
 * Single Responsibility: Solo contiene lo necesario para crear el ticket.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TicketCreateDTO {

    @NotNull(message = "El ID del cliente es obligatorio")
    private Long clienteId;

    @NotBlank(message = "El tipo de equipo es obligatorio")
    @Size(max = 100, message = "El tipo de equipo no puede exceder 100 caracteres")
    private String tipoEquipo;

    @NotBlank(message = "La marca es obligatoria")
    @Size(max = 100, message = "La marca no puede exceder 100 caracteres")
    private String marca;

    @Size(max = 100, message = "El modelo no puede exceder 100 caracteres")
    private String modelo;

    @Size(max = 100, message = "El número de serie no puede exceder 100 caracteres")
    private String numeroSerie;

    @NotBlank(message = "La falla reportada es obligatoria")
    @Size(max = 2000, message = "La falla reportada no puede exceder 2000 caracteres")
    private String fallaReportada;

    @Size(max = 500, message = "Los accesorios no pueden exceder 500 caracteres")
    private String accesorios;
}
