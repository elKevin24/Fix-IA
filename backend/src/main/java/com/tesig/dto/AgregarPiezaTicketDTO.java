package com.tesig.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO para agregar una pieza a un ticket.
 *
 * Aplicación de principios:
 * - Single Responsibility: Solo para transferir datos de agregar pieza a ticket
 * - Validation: Valida los datos de entrada
 *
 * @author TESIG System
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AgregarPiezaTicketDTO {

    @NotNull(message = "El ID de la pieza es obligatorio")
    private Long piezaId;

    @NotNull(message = "La cantidad es obligatoria")
    @Min(value = 1, message = "La cantidad debe ser al menos 1")
    private Integer cantidad;

    /**
     * Precio unitario opcional. Si no se proporciona, se usa el precio de venta actual de la pieza
     */
    @DecimalMin(value = "0.0", inclusive = false, message = "El precio unitario debe ser mayor a 0")
    @Digits(integer = 8, fraction = 2, message = "El precio unitario debe tener máximo 8 enteros y 2 decimales")
    private BigDecimal precioUnitario;

    @Size(max = 500, message = "Las notas no pueden exceder 500 caracteres")
    private String notas;
}
