package com.tesig.dto.ticket;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class AplicarDescuentoDTO {

    /**
     * Tipo de descuento: PORCENTAJE o MONTO
     */
    @NotBlank(message = "El tipo de descuento es requerido")
    private String tipoDescuento;

    /**
     * Valor del descuento (porcentaje o monto según el tipo)
     */
    @Positive(message = "El valor del descuento debe ser positivo")
    private BigDecimal valor;

    /**
     * Motivo del descuento
     */
    @NotBlank(message = "El motivo del descuento es requerido")
    private String motivo;
}
