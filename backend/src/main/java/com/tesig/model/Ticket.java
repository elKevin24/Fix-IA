package com.tesig.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "tickets", indexes = {
    @Index(name = "idx_ticket_numero", columnList = "numero_ticket"),
    @Index(name = "idx_ticket_estado", columnList = "estado"),
    @Index(name = "idx_ticket_cliente", columnList = "cliente_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Ticket extends BaseEntity {

    @NotBlank(message = "El número de ticket es obligatorio")
    @Column(name = "numero_ticket", nullable = false, unique = true, length = 20)
    private String numeroTicket;

    // Información del Equipo
    @NotBlank(message = "El tipo de equipo es obligatorio")
    @Column(nullable = false, length = 100)
    private String tipoEquipo;

    @NotBlank(message = "La marca es obligatoria")
    @Column(nullable = false, length = 100)
    private String marca;

    @Column(length = 100)
    private String modelo;

    @Column(name = "numero_serie", length = 100)
    private String numeroSerie;

    @NotBlank(message = "La falla reportada es obligatoria")
    @Column(name = "falla_reportada", nullable = false, columnDefinition = "TEXT")
    private String fallaReportada;

    @Column(columnDefinition = "TEXT")
    private String accesorios;

    // Estado y Seguimiento
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    @Builder.Default
    private EstadoTicket estado = EstadoTicket.INGRESADO;

    // Diagnóstico y Presupuesto
    @Column(columnDefinition = "TEXT")
    private String diagnostico;

    @Column(name = "presupuesto_mano_obra", precision = 10, scale = 2)
    private BigDecimal presupuestoManoObra;

    @Column(name = "presupuesto_piezas", precision = 10, scale = 2)
    private BigDecimal presupuestoPiezas;

    @Column(name = "presupuesto_total", precision = 10, scale = 2)
    private BigDecimal presupuestoTotal;

    @Column(name = "tiempo_estimado_dias")
    private Integer tiempoEstimadoDias;

    // Decisión del Cliente
    @Column(name = "fecha_presupuesto")
    private LocalDateTime fechaPresupuesto;

    @Column(name = "fecha_respuesta_cliente")
    private LocalDateTime fechaRespuestaCliente;

    @Column(name = "motivo_rechazo", columnDefinition = "TEXT")
    private String motivoRechazo;

    @Column(name = "motivo_cancelacion", columnDefinition = "TEXT")
    private String motivoCancelacion;

    // Reparación
    @Column(name = "observaciones_reparacion", columnDefinition = "TEXT")
    private String observacionesReparacion;

    @Column(name = "fecha_inicio_reparacion")
    private LocalDateTime fechaInicioReparacion;

    @Column(name = "fecha_fin_reparacion")
    private LocalDateTime fechaFinReparacion;

    @Column(name = "resultado_pruebas", columnDefinition = "TEXT")
    private String resultadoPruebas;

    // Entrega
    @Column(name = "fecha_entrega")
    private LocalDateTime fechaEntrega;

    @Column(name = "observaciones_entrega", columnDefinition = "TEXT")
    private String observacionesEntrega;

    // Relaciones
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id", nullable = false)
    @NotNull(message = "El cliente es obligatorio")
    private Cliente cliente;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tecnico_asignado_id")
    private Usuario tecnicoAsignado;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_ingreso_id")
    private Usuario usuarioIngreso;

    // Métodos de utilidad
    public void calcularPresupuestoTotal() {
        BigDecimal manoObra = presupuestoManoObra != null ? presupuestoManoObra : BigDecimal.ZERO;
        BigDecimal piezas = presupuestoPiezas != null ? presupuestoPiezas : BigDecimal.ZERO;
        this.presupuestoTotal = manoObra.add(piezas);
    }

    @PrePersist
    @PreUpdate
    private void calcularTotalAntesDeGuardar() {
        if (presupuestoManoObra != null || presupuestoPiezas != null) {
            calcularPresupuestoTotal();
        }
    }
}
