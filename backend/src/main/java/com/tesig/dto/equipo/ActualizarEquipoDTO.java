package com.tesig.dto.equipo;

import lombok.Data;

@Data
public class ActualizarEquipoDTO {
    private String tipoEquipo;
    private String marca;
    private String modelo;
    private String numeroSerie;
    private String accesorios;
    private String problemaReportado;
    private String diagnostico;
    private String trabajoRealizado;
}
