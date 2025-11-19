package com.tesig.dto.compra;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class CompraResponseDTO {
    private Long id;
    private String codigoCompra;
    private String proveedor;
    private String contactoProveedor;
    private LocalDate fechaCompra;
    private BigDecimal total;
    private String estado;
    private String observaciones;
    private List<DetalleDTO> detalles;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Data
    public static class DetalleDTO {
        private Long id;
        private Long piezaId;
        private String piezaCodigo;
        private String piezaNombre;
        private Integer cantidad;
        private BigDecimal precioUnitario;
        private BigDecimal subtotal;
    }
}
