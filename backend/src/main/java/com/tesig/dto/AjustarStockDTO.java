package com.tesig.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para ajustar el stock de una pieza (entrada o salida manual).
 *
 * Aplicación de principios:
 * - Single Responsibility: Solo para transferir datos de ajuste de stock
 * - Validation: Valida los datos de entrada
 *
 * @author TESIG System
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AjustarStockDTO {

    @NotNull(message = "El tipo de movimiento es obligatorio")
    @Pattern(regexp = "^(ENTRADA|SALIDA)$",
             message = "El tipo de movimiento debe ser ENTRADA o SALIDA")
    private String tipoMovimiento; // ENTRADA o SALIDA

    @NotNull(message = "La cantidad es obligatoria")
    @Min(value = 1, message = "La cantidad debe ser al menos 1")
    private Integer cantidad;

    @NotBlank(message = "El motivo es obligatorio")
    @Size(max = 500, message = "El motivo no puede exceder 500 caracteres")
    private String motivo;

    /**
     * Referencia opcional (número de orden de compra, ticket, etc.)
     */
    @Size(max = 100, message = "La referencia no puede exceder 100 caracteres")
    private String referencia;
}
