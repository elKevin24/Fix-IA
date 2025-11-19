package com.tesig.dto.equipo;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CrearEquipoDTO {

    @NotBlank(message = "El tipo de equipo es requerido")
    private String tipoEquipo;

    private String marca;
    private String modelo;
    private String numeroSerie;
    private String accesorios;

    @NotBlank(message = "El problema reportado es requerido")
    private String problemaReportado;
}
