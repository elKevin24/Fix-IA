package com.tesig.dto.compra;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
public class CrearCompraDTO {

    @NotBlank(message = "El proveedor es requerido")
    private String proveedor;

    private String contactoProveedor;

    @NotNull(message = "La fecha de compra es requerida")
    private LocalDate fechaCompra;

    private String observaciones;

    @NotEmpty(message = "Debe agregar al menos un detalle")
    private List<CompraDetalleDTO> detalles;

    @Data
    public static class CompraDetalleDTO {
        @NotNull(message = "La pieza es requerida")
        private Long piezaId;

        @NotNull(message = "La cantidad es requerida")
        @Positive(message = "La cantidad debe ser positiva")
        private Integer cantidad;

        @NotNull(message = "El precio unitario es requerido")
        @Positive(message = "El precio unitario debe ser positivo")
        private BigDecimal precioUnitario;
    }
}
