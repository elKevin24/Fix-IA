package com.tesig.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entidad que representa una pieza o repuesto en el inventario del taller.
 *
 * Aplicación de principios:
 * - Single Responsibility: Solo representa una pieza del inventario
 * - Encapsulación: Datos protegidos con getters/setters via Lombok
 *
 * @author TESIG System
 */
@Entity
@Table(name = "piezas", indexes = {
        @Index(name = "idx_pieza_codigo", columnList = "codigo", unique = true),
        @Index(name = "idx_pieza_nombre", columnList = "nombre"),
        @Index(name = "idx_pieza_categoria", columnList = "categoria"),
        @Index(name = "idx_pieza_stock", columnList = "stock"),
        @Index(name = "idx_pieza_deleted_at", columnList = "deleted_at")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Pieza {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Código único de la pieza (SKU - Stock Keeping Unit)
     * Ejemplo: "LCD-SAM-15.6", "BAT-DEL-5400", "HDD-WD-1TB"
     */
    @Column(nullable = false, unique = true, length = 50)
    private String codigo;

    /**
     * Nombre descriptivo de la pieza
     */
    @Column(nullable = false, length = 200)
    private String nombre;

    /**
     * Descripción detallada de la pieza
     */
    @Column(length = 1000)
    private String descripcion;

    /**
     * Categoría de la pieza
     * Ejemplos: "PANTALLA", "BATERIA", "DISCO_DURO", "MEMORIA_RAM", "TECLADO", etc.
     */
    @Column(nullable = false, length = 50)
    private String categoria;

    /**
     * Marca de la pieza o fabricante
     */
    @Column(length = 100)
    private String marca;

    /**
     * Modelo o número de parte del fabricante
     */
    @Column(length = 100)
    private String modelo;

    /**
     * Compatibilidad - equipos o modelos compatibles
     * Ejemplo: "Samsung Galaxy S20, S21", "Dell Inspiron 15 3000 Series"
     */
    @Column(length = 500)
    private String compatibilidad;

    /**
     * Precio de compra o costo de la pieza
     */
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal precioCosto;

    /**
     * Precio de venta al cliente (incluye margen de ganancia)
     */
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal precioVenta;

    /**
     * Cantidad actual en stock
     */
    @Column(nullable = false)
    private Integer stock;

    /**
     * Stock mínimo que genera alerta de reabastecimiento
     */
    @Column(nullable = false)
    private Integer stockMinimo;

    /**
     * Ubicación física en el almacén
     * Ejemplo: "Estante A-3", "Cajón B-12"
     */
    @Column(length = 100)
    private String ubicacion;

    /**
     * Proveedor principal de la pieza
     */
    @Column(length = 200)
    private String proveedor;

    /**
     * Teléfono del proveedor
     */
    @Column(length = 20)
    private String proveedorTelefono;

    /**
     * Email del proveedor
     */
    @Column(length = 100)
    private String proveedorEmail;

    /**
     * Notas adicionales sobre la pieza
     */
    @Column(length = 500)
    private String notas;

    /**
     * Indica si la pieza está activa en el sistema
     */
    @Column(nullable = false)
    @Builder.Default
    private Boolean activo = true;

    // ==================== CAMPOS DE AUDITORÍA ====================

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // ==================== MÉTODOS DE NEGOCIO ====================

    /**
     * Verifica si el stock actual está por debajo del mínimo
     */
    public boolean necesitaReabastecimiento() {
        return this.stock <= this.stockMinimo;
    }

    /**
     * Verifica si hay suficiente stock disponible
     */
    public boolean hayStockDisponible(int cantidadSolicitada) {
        return this.stock >= cantidadSolicitada;
    }

    /**
     * Reduce el stock (usado cuando se asigna a un ticket)
     */
    public void reducirStock(int cantidad) {
        if (cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor a 0");
        }
        if (this.stock < cantidad) {
            throw new IllegalStateException(
                    String.format("Stock insuficiente. Disponible: %d, Solicitado: %d",
                            this.stock, cantidad)
            );
        }
        this.stock -= cantidad;
    }

    /**
     * Aumenta el stock (usado cuando se recibe inventario)
     */
    public void aumentarStock(int cantidad) {
        if (cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor a 0");
        }
        this.stock += cantidad;
    }

    /**
     * Calcula el margen de ganancia en porcentaje
     */
    public BigDecimal calcularMargenGanancia() {
        if (precioCosto == null || precioCosto.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        BigDecimal ganancia = precioVenta.subtract(precioCosto);
        return ganancia.divide(precioCosto, 4, BigDecimal.ROUND_HALF_UP)
                .multiply(new BigDecimal("100"));
    }

    /**
     * Marca la pieza como eliminada (soft delete)
     */
    public void marcarComoEliminada() {
        this.deletedAt = LocalDateTime.now();
        this.activo = false;
    }

    /**
     * Verifica si la pieza está eliminada
     */
    public boolean isEliminada() {
        return this.deletedAt != null;
    }
}
