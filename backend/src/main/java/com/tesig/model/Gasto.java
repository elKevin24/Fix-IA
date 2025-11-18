package com.tesig.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "gastos")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Gasto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El concepto es requerido")
    @Size(max = 100, message = "El concepto no puede exceder 100 caracteres")
    @Column(nullable = false, length = 100)
    private String concepto;

    @Size(max = 500, message = "La descripción no puede exceder 500 caracteres")
    @Column(length = 500)
    private String descripcion;

    @NotNull(message = "El monto es requerido")
    @Positive(message = "El monto debe ser positivo")
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal monto;

    @NotNull(message = "La fecha es requerida")
    @Column(nullable = false)
    private LocalDate fecha;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private CategoriaGasto categoria;

    @Size(max = 100, message = "El proveedor no puede exceder 100 caracteres")
    @Column(length = 100)
    private String proveedor;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private MetodoPago metodoPago;

    @Size(max = 50, message = "El número de comprobante no puede exceder 50 caracteres")
    @Column(length = 50)
    private String numeroComprobante;

    @Column(name = "deleted", nullable = false)
    private Boolean deleted = false;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (deleted == null) {
            deleted = false;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public enum CategoriaGasto {
        ARRIENDO,
        SERVICIOS_PUBLICOS,
        SALARIOS,
        SUMINISTROS,
        MANTENIMIENTO,
        TRANSPORTE,
        MARKETING,
        IMPUESTOS,
        SEGUROS,
        OTROS
    }

    public enum MetodoPago {
        EFECTIVO,
        TRANSFERENCIA,
        CHEQUE,
        TARJETA,
        OTRO
    }
}
