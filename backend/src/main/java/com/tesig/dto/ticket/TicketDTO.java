package com.tesig.dto.ticket;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.tesig.dto.ClienteBasicoDTO;
import com.tesig.dto.EstadoTicketDTO;
import com.tesig.dto.auth.UserInfoDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO completo de Ticket para respuestas.
 * Incluye toda la información del ticket y relaciones.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TicketDTO {

    private Long id;
    private String numeroTicket;

    // Información del equipo
    private String tipoEquipo;
    private String marca;
    private String modelo;
    private String numeroSerie;
    private String fallaReportada;
    private String accesorios;

    // Estado
    private EstadoTicketDTO estado;

    // Diagnóstico y presupuesto
    private String diagnostico;
    private BigDecimal presupuestoManoObra;
    private BigDecimal presupuestoPiezas;
    private BigDecimal presupuestoTotal;
    private Integer tiempoEstimadoDias;

    // Decisión del cliente
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime fechaPresupuesto;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime fechaRespuestaCliente;

    private String motivoRechazo;
    private String motivoCancelacion;

    // Reparación
    private String observacionesReparacion;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime fechaInicioReparacion;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime fechaFinReparacion;

    private String resultadoPruebas;

    // Entrega
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime fechaEntrega;

    private String observacionesEntrega;

    // Relaciones
    private ClienteBasicoDTO cliente;
    private UserInfoDTO tecnicoAsignado;
    private UserInfoDTO usuarioIngreso;

    // Auditoría
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;
}
