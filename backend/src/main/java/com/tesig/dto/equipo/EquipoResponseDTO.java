package com.tesig.dto.equipo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class EquipoResponseDTO {
    private Long id;
    private Long ticketId;
    private String tipoEquipo;
    private String marca;
    private String modelo;
    private String numeroSerie;
    private String accesorios;
    private String problemaReportado;
    private String diagnostico;
    private String trabajoRealizado;
    private LocalDateTime createdAt;
}
