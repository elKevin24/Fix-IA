package com.tesig.controller;

import com.tesig.dto.ApiResponse;
import com.tesig.dto.ReporteFinancieroDTO;
import com.tesig.service.IReporteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

/**
 * Controlador REST para generación de reportes financieros.
 *
 * @author TESIG System
 */
@RestController
@RequestMapping("/api/reportes")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Reportes", description = "Endpoints para generación de reportes financieros")
@SecurityRequirement(name = "bearer-token")
public class ReporteController {

    private final IReporteService reporteService;

    // ==================== REPORTES FINANCIEROS ====================

    @Operation(
        summary = "Generar reporte financiero",
        description = "Genera un reporte financiero completo para el rango de fechas especificado. " +
                     "Incluye ingresos, gastos, utilidad e indicadores de inventario."
    )
    @GetMapping("/financiero")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<ApiResponse<ReporteFinancieroDTO>> generarReporteFinanciero(
            @Parameter(description = "Fecha de inicio del período")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @Parameter(description = "Fecha de fin del período")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin
    ) {
        log.info("GET /api/reportes/financiero?fechaInicio={}&fechaFin={}", fechaInicio, fechaFin);

        ReporteFinancieroDTO reporte = reporteService.generarReporteFinanciero(fechaInicio, fechaFin);

        return ResponseEntity.ok(
                ApiResponse.success("Reporte financiero generado exitosamente", reporte)
        );
    }

    @Operation(
        summary = "Obtener reporte del mes actual",
        description = "Genera un reporte financiero con los datos del mes en curso"
    )
    @GetMapping("/financiero/mes-actual")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<ApiResponse<ReporteFinancieroDTO>> getReporteMesActual() {
        log.info("GET /api/reportes/financiero/mes-actual");

        ReporteFinancieroDTO reporte = reporteService.getReporteMesActual();

        return ResponseEntity.ok(
                ApiResponse.success("Reporte del mes actual generado", reporte)
        );
    }

    @Operation(
        summary = "Obtener reporte anual",
        description = "Genera un reporte financiero con los datos del año en curso"
    )
    @GetMapping("/financiero/anual")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<ApiResponse<ReporteFinancieroDTO>> getReporteAnual() {
        log.info("GET /api/reportes/financiero/anual");

        ReporteFinancieroDTO reporte = reporteService.getReporteAnual();

        return ResponseEntity.ok(
                ApiResponse.success("Reporte anual generado", reporte)
        );
    }

    @Operation(
        summary = "Obtener reporte comparativo",
        description = "Genera un reporte financiero comparativo para el período especificado"
    )
    @GetMapping("/financiero/comparativo")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<ApiResponse<ReporteFinancieroDTO>> getReporteComparativo(
            @Parameter(description = "Fecha de inicio")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @Parameter(description = "Fecha de fin")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin
    ) {
        log.info("GET /api/reportes/financiero/comparativo?fechaInicio={}&fechaFin={}",
                 fechaInicio, fechaFin);

        ReporteFinancieroDTO reporte = reporteService.getReporteComparativo(fechaInicio, fechaFin);

        return ResponseEntity.ok(
                ApiResponse.success("Reporte comparativo generado", reporte)
        );
    }
}
