package com.tesig.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO para respuesta de consulta de pieza.
 * Contiene toda la información de la pieza para mostrar al cliente.
 *
 * Aplicación de principios:
 * - Single Responsibility: Solo para transferir datos de respuesta de pieza
 * - Information Hiding: Expone solo lo necesario al cliente
 *
 * @author TESIG System
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PiezaResponseDTO {

    private Long id;
    private String codigo;
    private String nombre;
    private String descripcion;
    private String categoria;
    private String marca;
    private String modelo;
    private String compatibilidad;
    private BigDecimal precioCosto;
    private BigDecimal precioVenta;
    private Integer stock;
    private Integer stockMinimo;
    private String ubicacion;
    private String proveedor;
    private String proveedorTelefono;
    private String proveedorEmail;
    private String notas;
    private Boolean activo;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Campos calculados
    private Boolean necesitaReabastecimiento;
    private BigDecimal margenGanancia;
    private String estadoStock; // "SIN_STOCK", "STOCK_BAJO", "STOCK_NORMAL"

    /**
     * Calcula el estado del stock
     */
    public void calcularEstadoStock() {
        if (stock == null || stock == 0) {
            this.estadoStock = "SIN_STOCK";
        } else if (stockMinimo != null && stock <= stockMinimo) {
            this.estadoStock = "STOCK_BAJO";
        } else {
            this.estadoStock = "STOCK_NORMAL";
        }
    }

    /**
     * Calcula el margen de ganancia
     */
    public void calcularMargenGanancia() {
        if (precioCosto == null || precioCosto.compareTo(BigDecimal.ZERO) == 0) {
            this.margenGanancia = BigDecimal.ZERO;
            return;
        }
        BigDecimal ganancia = precioVenta.subtract(precioCosto);
        this.margenGanancia = ganancia.divide(precioCosto, 4, BigDecimal.ROUND_HALF_UP)
                .multiply(new BigDecimal("100"));
    }

    /**
     * Determina si necesita reabastecimiento
     */
    public void calcularNecesitaReabastecimiento() {
        this.necesitaReabastecimiento = (stock != null && stockMinimo != null && stock <= stockMinimo);
    }
}
