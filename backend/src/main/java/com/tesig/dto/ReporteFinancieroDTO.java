package com.tesig.dto;

import lombok.Data;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReporteFinancieroDTO {

    private LocalDate fechaInicio;
    private LocalDate fechaFin;

    // Ingresos
    private BigDecimal ingresosTotales;
    private BigDecimal ingresosManoObra;
    private BigDecimal ingresosPiezas;
    private Integer ticketsCompletados;
    private BigDecimal ticketPromedio;

    // Gastos
    private BigDecimal gastosTotales;
    private BigDecimal gastosCompras;
    private BigDecimal gastosOperativos;
    private Map<String, BigDecimal> gastosPorCategoria;

    // Utilidad
    private BigDecimal utilidadBruta;
    private BigDecimal utilidadNeta;
    private BigDecimal margenUtilidad;

    // Inventario
    private BigDecimal valorInventario;
    private Integer piezasSinStock;
    private Integer piezasStockBajo;

    // Tendencias
    private List<ResumenMensual> tendenciaMensual;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ResumenMensual {
        private String mes;
        private BigDecimal ingresos;
        private BigDecimal gastos;
        private BigDecimal utilidad;
        private Integer tickets;
    }
}
