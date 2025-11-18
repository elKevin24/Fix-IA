package com.tesig.controller;

import com.tesig.dto.ApiResponse;
import com.tesig.dto.gasto.CrearGastoDTO;
import com.tesig.dto.gasto.GastoResponseDTO;
import com.tesig.service.IGastoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Controlador REST para gestión de gastos operativos.
 *
 * @author TESIG System
 */
@RestController
@RequestMapping("/api/gastos")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Gastos", description = "Endpoints para gestión de gastos operativos")
@SecurityRequirement(name = "bearer-token")
public class GastoController {

    private final IGastoService gastoService;

    // ==================== CRUD BÁSICO ====================

    @Operation(
        summary = "Crear nuevo gasto",
        description = "Registra un nuevo gasto operativo. " +
                     "Solo accesible para ADMINISTRADOR."
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "201",
            description = "Gasto creado exitosamente",
            content = @Content(schema = @Schema(implementation = GastoResponseDTO.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "Datos inválidos"
        )
    })
    @PostMapping
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<ApiResponse<GastoResponseDTO>> crear(
            @Valid @RequestBody CrearGastoDTO dto
    ) {
        log.info("POST /api/gastos - Crear gasto: {}", dto.getConcepto());

        GastoResponseDTO gasto = gastoService.crear(dto);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Gasto creado exitosamente", gasto));
    }

    @Operation(
        summary = "Actualizar gasto",
        description = "Actualiza un gasto existente. Solo accesible para ADMINISTRADOR."
    )
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<ApiResponse<GastoResponseDTO>> actualizar(
            @Parameter(description = "ID del gasto")
            @PathVariable Long id,
            @Valid @RequestBody CrearGastoDTO dto
    ) {
        log.info("PUT /api/gastos/{} - Actualizar gasto", id);

        GastoResponseDTO gasto = gastoService.actualizar(id, dto);

        return ResponseEntity.ok(
                ApiResponse.success("Gasto actualizado exitosamente", gasto)
        );
    }

    @Operation(
        summary = "Obtener gasto por ID",
        description = "Obtiene los detalles de un gasto específico"
    )
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECEPCIONISTA')")
    public ResponseEntity<ApiResponse<GastoResponseDTO>> obtenerPorId(
            @Parameter(description = "ID del gasto")
            @PathVariable Long id
    ) {
        log.info("GET /api/gastos/{} - Obtener gasto", id);

        GastoResponseDTO gasto = gastoService.getById(id);

        return ResponseEntity.ok(
                ApiResponse.success("Gasto encontrado", gasto)
        );
    }

    @Operation(
        summary = "Listar todos los gastos",
        description = "Lista todos los gastos con paginación"
    )
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECEPCIONISTA')")
    public ResponseEntity<ApiResponse<Page<GastoResponseDTO>>> listarTodos(
            @PageableDefault(size = 20, sort = "fecha") Pageable pageable
    ) {
        log.info("GET /api/gastos - Listar todos los gastos - Página: {}", pageable.getPageNumber());

        Page<GastoResponseDTO> gastos = gastoService.getAll(pageable);

        return ResponseEntity.ok(
                ApiResponse.success("Gastos obtenidos exitosamente", gastos)
        );
    }

    @Operation(
        summary = "Eliminar gasto",
        description = "Elimina un gasto (soft delete). Solo accesible para ADMINISTRADOR."
    )
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<ApiResponse<Void>> eliminar(
            @Parameter(description = "ID del gasto")
            @PathVariable Long id
    ) {
        log.info("DELETE /api/gastos/{} - Eliminar gasto", id);

        gastoService.eliminar(id);

        return ResponseEntity.ok(
                ApiResponse.success("Gasto eliminado exitosamente", null)
        );
    }

    // ==================== BÚSQUEDAS Y FILTROS ====================

    @Operation(
        summary = "Obtener gastos por categoría",
        description = "Lista gastos filtrados por categoría"
    )
    @GetMapping("/categoria/{categoria}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECEPCIONISTA')")
    public ResponseEntity<ApiResponse<Page<GastoResponseDTO>>> obtenerPorCategoria(
            @Parameter(description = "Categoría del gasto")
            @PathVariable String categoria,
            @PageableDefault(size = 20) Pageable pageable
    ) {
        log.info("GET /api/gastos/categoria/{}", categoria);

        Page<GastoResponseDTO> gastos = gastoService.getByCategoria(categoria, pageable);

        return ResponseEntity.ok(
                ApiResponse.success("Gastos obtenidos", gastos)
        );
    }

    @Operation(
        summary = "Obtener gastos por rango de fechas",
        description = "Lista gastos dentro del rango de fechas especificado"
    )
    @GetMapping("/fechas")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECEPCIONISTA')")
    public ResponseEntity<ApiResponse<List<GastoResponseDTO>>> obtenerPorFechas(
            @Parameter(description = "Fecha de inicio")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @Parameter(description = "Fecha de fin")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin
    ) {
        log.info("GET /api/gastos/fechas?fechaInicio={}&fechaFin={}", fechaInicio, fechaFin);

        List<GastoResponseDTO> gastos = gastoService.getByFechas(fechaInicio, fechaFin);

        return ResponseEntity.ok(
                ApiResponse.success("Gastos obtenidos", gastos)
        );
    }

    // ==================== TOTALES ====================

    @Operation(
        summary = "Obtener total de gastos en un período",
        description = "Calcula el total de gastos en el rango de fechas especificado"
    )
    @GetMapping("/total")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECEPCIONISTA')")
    public ResponseEntity<ApiResponse<BigDecimal>> obtenerTotalGastos(
            @Parameter(description = "Fecha de inicio")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @Parameter(description = "Fecha de fin")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin
    ) {
        log.info("GET /api/gastos/total?fechaInicio={}&fechaFin={}", fechaInicio, fechaFin);

        BigDecimal total = gastoService.getTotalGastos(fechaInicio, fechaFin);

        return ResponseEntity.ok(
                ApiResponse.success("Total calculado", total)
        );
    }

    // ==================== CATEGORÍAS ====================

    @Operation(
        summary = "Obtener categorías de gastos",
        description = "Lista todas las categorías de gastos disponibles"
    )
    @GetMapping("/categorias")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECEPCIONISTA')")
    public ResponseEntity<ApiResponse<List<String>>> obtenerCategorias() {
        log.info("GET /api/gastos/categorias");

        List<String> categorias = gastoService.getCategorias();

        return ResponseEntity.ok(
                ApiResponse.success("Categorías obtenidas", categorias)
        );
    }
}
