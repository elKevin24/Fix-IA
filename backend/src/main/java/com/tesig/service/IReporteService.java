package com.tesig.service;

import com.tesig.dto.ReporteFinancieroDTO;

import java.time.LocalDate;

public interface IReporteService {

    /**
     * Genera reporte financiero completo para un período
     */
    ReporteFinancieroDTO generarReporteFinanciero(LocalDate fechaInicio, LocalDate fechaFin);

    /**
     * Genera reporte del mes actual
     */
    ReporteFinancieroDTO getReporteMesActual();

    /**
     * Genera reporte del año actual
     */
    ReporteFinancieroDTO getReporteAnual();

    /**
     * Genera reporte comparativo con período anterior
     */
    ReporteFinancieroDTO getReporteComparativo(LocalDate fechaInicio, LocalDate fechaFin);
}
