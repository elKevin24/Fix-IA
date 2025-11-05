package com.tesig.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO para consulta pública de tickets.
 * Contiene información visible para el cliente sin datos sensibles.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TicketConsultaPublicaDTO {

    private String numeroTicket;

    // Información del equipo
    private String tipoEquipo;
    private String marca;
    private String modelo;
    private String fallaReportada;

    // Estado actual
    private EstadoTicketDTO estado;

    // Cliente
    private ClienteBasicoDTO cliente;

    // Fechas importantes
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime fechaIngreso;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime fechaPresupuesto;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime fechaInicioReparacion;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime fechaFinReparacion;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime fechaEntrega;

    // Información de presupuesto (si existe)
    private BigDecimal presupuestoTotal;
    private Integer tiempoEstimadoDias;

    // Diagnóstico (si existe y está presupuestado)
    private String diagnostico;

    // Observaciones finales (si está listo o entregado)
    private String observacionesEntrega;

}
