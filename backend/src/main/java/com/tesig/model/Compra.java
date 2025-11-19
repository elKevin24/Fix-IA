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
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "compras")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Compra {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El código de compra es requerido")
    @Size(max = 50, message = "El código no puede exceder 50 caracteres")
    @Column(nullable = false, unique = true, length = 50)
    private String codigoCompra;

    @NotBlank(message = "El proveedor es requerido")
    @Size(max = 100, message = "El proveedor no puede exceder 100 caracteres")
    @Column(nullable = false, length = 100)
    private String proveedor;

    @Size(max = 200, message = "El contacto no puede exceder 200 caracteres")
    @Column(length = 200)
    private String contactoProveedor;

    @NotNull(message = "La fecha de compra es requerida")
    @Column(nullable = false)
    private LocalDate fechaCompra;

    @NotNull(message = "El total es requerido")
    @Positive(message = "El total debe ser positivo")
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal total;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoCompra estado;

    @Size(max = 500, message = "Las observaciones no pueden exceder 500 caracteres")
    @Column(length = 500)
    private String observaciones;

    @OneToMany(mappedBy = "compra", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<CompraDetalle> detalles = new ArrayList<>();

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
        if (estado == null) {
            estado = EstadoCompra.PENDIENTE;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public enum EstadoCompra {
        PENDIENTE,
        RECIBIDA,
        CANCELADA
    }
}
