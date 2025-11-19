package com.tesig.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entidad que representa la relación entre un Ticket y las Piezas utilizadas.
 * Esta es una tabla de asociación que almacena información adicional sobre
 * la pieza en el contexto del ticket (cantidad, precio al momento del uso).
 *
 * Aplicación de principios:
 * - Single Responsibility: Solo gestiona la relación ticket-pieza
 * - Open/Closed: Permite extender funcionalidad sin modificar código existente
 *
 * @author TESIG System
 */
@Entity
@Table(name = "ticket_piezas", indexes = {
        @Index(name = "idx_ticket_pieza_ticket", columnList = "ticket_id"),
        @Index(name = "idx_ticket_pieza_pieza", columnList = "pieza_id"),
        @Index(name = "idx_ticket_pieza_created", columnList = "created_at")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TicketPieza {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Ticket al que se le están asignando las piezas
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ticket_id", nullable = false)
    private Ticket ticket;

    /**
     * Pieza que se está utilizando en la reparación
     */
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "pieza_id", nullable = false)
    private Pieza pieza;

    /**
     * Cantidad de piezas utilizadas
     */
    @Column(nullable = false)
    private Integer cantidad;

    /**
     * Precio unitario de la pieza al momento de ser agregada al ticket.
     * Se almacena aquí para mantener un histórico correcto,
     * ya que el precio de venta de la pieza puede cambiar en el futuro.
     */
    @Column(name = "precio_unitario", nullable = false, precision = 10, scale = 2)
    private BigDecimal precioUnitario;

    /**
     * Subtotal calculado (cantidad * precioUnitario)
     * Se almacena para optimizar consultas y mantener histórico
     */
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal subtotal;

    /**
     * Indica si esta pieza ya fue descontada del inventario
     */
    @Column(name = "stock_descontado", nullable = false)
    @Builder.Default
    private Boolean stockDescontado = false;

    /**
     * Notas específicas sobre el uso de esta pieza en este ticket
     * Ejemplo: "Pieza defectuosa devuelta al proveedor", "Cliente trajo su propia pieza"
     */
    @Column(length = 500)
    private String notas;

    // ==================== CAMPOS DE AUDITORÍA ====================

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();

        // Calcular subtotal si no está establecido
        if (subtotal == null && precioUnitario != null && cantidad != null) {
            calcularSubtotal();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();

        // Recalcular subtotal si cambió cantidad o precio
        if (precioUnitario != null && cantidad != null) {
            calcularSubtotal();
        }
    }

    // ==================== MÉTODOS DE NEGOCIO ====================

    /**
     * Calcula el subtotal (cantidad * precio unitario)
     */
    public void calcularSubtotal() {
        if (this.cantidad == null || this.precioUnitario == null) {
            throw new IllegalStateException("No se puede calcular el subtotal sin cantidad y precio");
        }
        this.subtotal = this.precioUnitario.multiply(new BigDecimal(this.cantidad));
    }

    /**
     * Establece la cantidad y recalcula el subtotal
     */
    public void setCantidadYCalcular(Integer cantidad) {
        this.cantidad = cantidad;
        calcularSubtotal();
    }

    /**
     * Establece el precio unitario y recalcula el subtotal
     */
    public void setPrecioUnitarioYCalcular(BigDecimal precioUnitario) {
        this.precioUnitario = precioUnitario;
        calcularSubtotal();
    }

    /**
     * Descuenta la pieza del inventario
     * Este método debe ser llamado cuando se confirma el uso de la pieza
     */
    public void descontarDelInventario() {
        if (stockDescontado) {
            throw new IllegalStateException("El stock ya fue descontado para esta pieza");
        }

        if (pieza == null) {
            throw new IllegalStateException("No se puede descontar sin una pieza asociada");
        }

        pieza.reducirStock(this.cantidad);
        this.stockDescontado = true;
    }

    /**
     * Reintegra la pieza al inventario (por ejemplo, si se cancela la reparación)
     */
    public void reintegrarAlInventario() {
        if (!stockDescontado) {
            throw new IllegalStateException("El stock no ha sido descontado previamente");
        }

        if (pieza == null) {
            throw new IllegalStateException("No se puede reintegrar sin una pieza asociada");
        }

        pieza.aumentarStock(this.cantidad);
        this.stockDescontado = false;
    }

    /**
     * Valida que la pieza tenga stock suficiente antes de asignar
     */
    public boolean validarStockDisponible() {
        if (pieza == null) {
            return false;
        }
        return pieza.hayStockDisponible(this.cantidad);
    }

    /**
     * Obtiene el total con formato para mostrar
     */
    public String getSubtotalFormateado() {
        if (subtotal == null) {
            return "$0.00";
        }
        return String.format("$%.2f", subtotal);
    }
}
