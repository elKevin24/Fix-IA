package com.tesig.dto.gasto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class CrearGastoDTO {

    @NotBlank(message = "El concepto es requerido")
    private String concepto;

    private String descripcion;

    @NotNull(message = "El monto es requerido")
    @Positive(message = "El monto debe ser positivo")
    private BigDecimal monto;

    @NotNull(message = "La fecha es requerida")
    private LocalDate fecha;

    @NotBlank(message = "La categoría es requerida")
    private String categoria;

    private String proveedor;

    @NotBlank(message = "El método de pago es requerido")
    private String metodoPago;

    private String numeroComprobante;
}
