package com.tesig.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "equipos")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Equipo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ticket_id", nullable = false)
    private Ticket ticket;

    @NotBlank(message = "El tipo de equipo es requerido")
    @Size(max = 50, message = "El tipo de equipo no puede exceder 50 caracteres")
    @Column(nullable = false, length = 50)
    private String tipoEquipo;

    @Size(max = 50, message = "La marca no puede exceder 50 caracteres")
    @Column(length = 50)
    private String marca;

    @Size(max = 50, message = "El modelo no puede exceder 50 caracteres")
    @Column(length = 50)
    private String modelo;

    @Size(max = 100, message = "El número de serie no puede exceder 100 caracteres")
    @Column(length = 100)
    private String numeroSerie;

    @Size(max = 500, message = "Los accesorios no pueden exceder 500 caracteres")
    @Column(length = 500)
    private String accesorios;

    @NotBlank(message = "El problema reportado es requerido")
    @Size(max = 1000, message = "El problema reportado no puede exceder 1000 caracteres")
    @Column(nullable = false, length = 1000)
    private String problemaReportado;

    @Size(max = 1000, message = "El diagnóstico no puede exceder 1000 caracteres")
    @Column(length = 1000)
    private String diagnostico;

    @Size(max = 1000, message = "El trabajo realizado no puede exceder 1000 caracteres")
    @Column(length = 1000)
    private String trabajoRealizado;

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
}
