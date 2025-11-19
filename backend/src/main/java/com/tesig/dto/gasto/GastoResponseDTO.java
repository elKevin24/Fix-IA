package com.tesig.dto.gasto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class GastoResponseDTO {
    private Long id;
    private String concepto;
    private String descripcion;
    private BigDecimal monto;
    private LocalDate fecha;
    private String categoria;
    private String proveedor;
    private String metodoPago;
    private String numeroComprobante;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
