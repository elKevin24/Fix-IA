package com.tesig.dto.ticket;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO para registrar diagnóstico y presupuesto.
 * Single Responsibility: Solo información de diagnóstico.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DiagnosticoDTO {

    @NotBlank(message = "El diagnóstico es obligatorio")
    @Size(max = 2000, message = "El diagnóstico no puede exceder 2000 caracteres")
    private String diagnostico;

    @NotNull(message = "El presupuesto de mano de obra es obligatorio")
    @DecimalMin(value = "0.0", message = "El presupuesto de mano de obra debe ser mayor o igual a 0")
    private BigDecimal presupuestoManoObra;

    @NotNull(message = "El presupuesto de piezas es obligatorio")
    @DecimalMin(value = "0.0", message = "El presupuesto de piezas debe ser mayor o igual a 0")
    private BigDecimal presupuestoPiezas;

    @NotNull(message = "El tiempo estimado es obligatorio")
    @Min(value = 1, message = "El tiempo estimado debe ser al menos 1 día")
    @Max(value = 365, message = "El tiempo estimado no puede exceder 365 días")
    private Integer tiempoEstimadoDias;
}
