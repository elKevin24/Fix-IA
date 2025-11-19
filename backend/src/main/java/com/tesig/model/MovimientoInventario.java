package com.tesig.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "movimientos_inventario")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MovimientoInventario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pieza_id", nullable = false)
    private Pieza pieza;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TipoMovimiento tipoMovimiento;

    @NotNull(message = "La cantidad es requerida")
    @Column(nullable = false)
    private Integer cantidad;

    @NotNull(message = "El stock anterior es requerido")
    @Column(nullable = false)
    private Integer stockAnterior;

    @NotNull(message = "El stock nuevo es requerido")
    @Column(nullable = false)
    private Integer stockNuevo;

    @Size(max = 500, message = "La descripción no puede exceder 500 caracteres")
    @Column(length = 500)
    private String descripcion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "compra_id")
    private Compra compra;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ticket_id")
    private Ticket ticket;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    public enum TipoMovimiento {
        COMPRA,           // Entrada por compra
        AJUSTE_ENTRADA,   // Ajuste manual positivo
        AJUSTE_SALIDA,    // Ajuste manual negativo
        USO_TICKET,       // Salida por uso en ticket
        DEVOLUCION        // Devolución (retorno al inventario)
    }
}
