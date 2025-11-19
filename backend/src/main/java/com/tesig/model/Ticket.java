package com.tesig.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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

    /**
     * Lista de piezas utilizadas en la reparación de este ticket
     */
    @OneToMany(mappedBy = "ticket", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<TicketPieza> piezasUtilizadas = new ArrayList<>();

    /**
     * Lista de equipos asociados a este ticket (para múltiples equipos)
     */
    @OneToMany(mappedBy = "ticket", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Equipo> equipos = new ArrayList<>();

    // Descuentos
    @Column(name = "descuento_porcentaje", precision = 5, scale = 2)
    private BigDecimal descuentoPorcentaje;

    @Column(name = "descuento_monto", precision = 10, scale = 2)
    private BigDecimal descuentoMonto;

    @Column(name = "motivo_descuento", length = 500)
    private String motivoDescuento;

    @Column(name = "total_con_descuento", precision = 10, scale = 2)
    private BigDecimal totalConDescuento;

    // Métodos de utilidad

    /**
     * Agrega una pieza utilizada al ticket
     */
    public void agregarPieza(TicketPieza ticketPieza) {
        piezasUtilizadas.add(ticketPieza);
        ticketPieza.setTicket(this);
    }

    /**
     * Remueve una pieza del ticket
     */
    public void removerPieza(TicketPieza ticketPieza) {
        piezasUtilizadas.remove(ticketPieza);
        ticketPieza.setTicket(null);
    }

    /**
     * Calcula el total de las piezas utilizadas
     */
    public BigDecimal calcularTotalPiezas() {
        return piezasUtilizadas.stream()
                .map(TicketPieza::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * Actualiza el presupuesto de piezas basado en las piezas utilizadas
     */
    public void actualizarPresupuestoPiezas() {
        this.presupuestoPiezas = calcularTotalPiezas();
    }

    public void calcularPresupuestoTotal() {
        BigDecimal manoObra = presupuestoManoObra != null ? presupuestoManoObra : BigDecimal.ZERO;
        BigDecimal piezas = presupuestoPiezas != null ? presupuestoPiezas : BigDecimal.ZERO;
        this.presupuestoTotal = manoObra.add(piezas);
    }

    /**
     * Agrega un equipo al ticket
     */
    public void agregarEquipo(Equipo equipo) {
        equipos.add(equipo);
        equipo.setTicket(this);
    }

    /**
     * Remueve un equipo del ticket
     */
    public void removerEquipo(Equipo equipo) {
        equipos.remove(equipo);
        equipo.setTicket(null);
    }

    /**
     * Aplica descuento por porcentaje
     */
    public void aplicarDescuentoPorcentaje(BigDecimal porcentaje, String motivo) {
        if (presupuestoTotal == null || presupuestoTotal.compareTo(BigDecimal.ZERO) == 0) {
            return;
        }
        this.descuentoPorcentaje = porcentaje;
        this.descuentoMonto = presupuestoTotal.multiply(porcentaje).divide(BigDecimal.valueOf(100));
        this.motivoDescuento = motivo;
        calcularTotalConDescuento();
    }

    /**
     * Aplica descuento por monto fijo
     */
    public void aplicarDescuentoMonto(BigDecimal monto, String motivo) {
        if (presupuestoTotal == null || presupuestoTotal.compareTo(BigDecimal.ZERO) == 0) {
            return;
        }
        this.descuentoMonto = monto;
        this.descuentoPorcentaje = monto.multiply(BigDecimal.valueOf(100)).divide(presupuestoTotal, 2, java.math.RoundingMode.HALF_UP);
        this.motivoDescuento = motivo;
        calcularTotalConDescuento();
    }

    /**
     * Calcula el total con descuento
     */
    public void calcularTotalConDescuento() {
        BigDecimal total = presupuestoTotal != null ? presupuestoTotal : BigDecimal.ZERO;
        BigDecimal descuento = descuentoMonto != null ? descuentoMonto : BigDecimal.ZERO;
        this.totalConDescuento = total.subtract(descuento);
        if (this.totalConDescuento.compareTo(BigDecimal.ZERO) < 0) {
            this.totalConDescuento = BigDecimal.ZERO;
        }
    }

    /**
     * Obtiene el total final (con descuento si aplica, sino el presupuesto total)
     */
    public BigDecimal getTotalFinal() {
        if (totalConDescuento != null && totalConDescuento.compareTo(BigDecimal.ZERO) > 0) {
            return totalConDescuento;
        }
        return presupuestoTotal != null ? presupuestoTotal : BigDecimal.ZERO;
    }

    @PrePersist
    @PreUpdate
    private void calcularTotalAntesDeGuardar() {
        if (presupuestoManoObra != null || presupuestoPiezas != null) {
            calcularPresupuestoTotal();
        }
        if (descuentoMonto != null || descuentoPorcentaje != null) {
            calcularTotalConDescuento();
        }
    }
}
