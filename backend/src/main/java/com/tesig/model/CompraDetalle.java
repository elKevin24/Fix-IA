package com.tesig.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "compra_detalles")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CompraDetalle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "compra_id", nullable = false)
    private Compra compra;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pieza_id", nullable = false)
    private Pieza pieza;

    @NotNull(message = "La cantidad es requerida")
    @Positive(message = "La cantidad debe ser positiva")
    @Column(nullable = false)
    private Integer cantidad;

    @NotNull(message = "El precio unitario es requerido")
    @Positive(message = "El precio unitario debe ser positivo")
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal precioUnitario;

    @NotNull(message = "El subtotal es requerido")
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal subtotal;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        calcularSubtotal();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
        calcularSubtotal();
    }

    private void calcularSubtotal() {
        if (cantidad != null && precioUnitario != null) {
            subtotal = precioUnitario.multiply(BigDecimal.valueOf(cantidad));
        }
    }
}
