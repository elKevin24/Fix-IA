package com.tesig.controller;

import com.tesig.dto.ApiResponse;
import com.tesig.model.ConfiguracionEmpresa;
import com.tesig.repository.ConfiguracionEmpresaRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador REST para gestión de configuración de empresa.
 *
 * @author TESIG System
 */
@RestController
@RequestMapping("/api/configuracion")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Configuración", description = "Endpoints para configuración de la empresa")
@SecurityRequirement(name = "bearer-token")
public class ConfiguracionEmpresaController {

    private final ConfiguracionEmpresaRepository configuracionRepository;

    @Operation(
        summary = "Obtener configuración activa",
        description = "Obtiene la configuración activa de la empresa"
    )
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECEPCIONISTA', 'TECNICO')")
    public ResponseEntity<ApiResponse<ConfiguracionEmpresa>> obtenerConfiguracion() {
        log.info("GET /api/configuracion - Obteniendo configuración activa");

        ConfiguracionEmpresa config = configuracionRepository.findFirstActiveConfiguration()
                .orElse(null);

        if (config == null) {
            return ResponseEntity.ok(
                    ApiResponse.success("No hay configuración registrada", null)
            );
        }

        return ResponseEntity.ok(
                ApiResponse.success("Configuración obtenida", config)
        );
    }

    @Operation(
        summary = "Crear configuración",
        description = "Crea una nueva configuración de empresa. Solo ADMINISTRADOR."
    )
    @PostMapping
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<ApiResponse<ConfiguracionEmpresa>> crear(
            @Valid @RequestBody ConfiguracionEmpresa config
    ) {
        log.info("POST /api/configuracion - Crear configuración para: {}", config.getNombreEmpresa());

        // Desactivar configuraciones anteriores
        configuracionRepository.findByActivoTrue().ifPresent(existing -> {
            existing.setActivo(false);
            configuracionRepository.save(existing);
        });

        config.setActivo(true);
        ConfiguracionEmpresa saved = configuracionRepository.save(config);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Configuración creada exitosamente", saved));
    }

    @Operation(
        summary = "Actualizar configuración",
        description = "Actualiza la configuración de empresa existente. Solo ADMINISTRADOR."
    )
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<ApiResponse<ConfiguracionEmpresa>> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody ConfiguracionEmpresa config
    ) {
        log.info("PUT /api/configuracion/{} - Actualizar configuración", id);

        ConfiguracionEmpresa existing = configuracionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Configuración no encontrada: " + id));

        // Actualizar campos
        existing.setNombreEmpresa(config.getNombreEmpresa());
        existing.setRazonSocial(config.getRazonSocial());
        existing.setIdentificacionFiscal(config.getIdentificacionFiscal());
        existing.setCodigoEmpresa(config.getCodigoEmpresa());
        existing.setCodigoSucursal(config.getCodigoSucursal());
        existing.setNombreSucursal(config.getNombreSucursal());
        existing.setDireccion(config.getDireccion());
        existing.setCiudad(config.getCiudad());
        existing.setProvincia(config.getProvincia());
        existing.setCodigoPostal(config.getCodigoPostal());
        existing.setPais(config.getPais());
        existing.setTelefonoPrincipal(config.getTelefonoPrincipal());
        existing.setTelefonoSecundario(config.getTelefonoSecundario());
        existing.setEmailContacto(config.getEmailContacto());
        existing.setEmailSoporte(config.getEmailSoporte());
        existing.setSitioWeb(config.getSitioWeb());
        existing.setHorarioAtencion(config.getHorarioAtencion());
        existing.setDiasLaborales(config.getDiasLaborales());
        existing.setPrefijoTicket(config.getPrefijoTicket());
        existing.setLongitudSecuencia(config.getLongitudSecuencia());
        existing.setDiasGarantiaDefault(config.getDiasGarantiaDefault());
        existing.setPorcentajeImpuesto(config.getPorcentajeImpuesto());
        existing.setMoneda(config.getMoneda());
        existing.setMensajeBienvenida(config.getMensajeBienvenida());
        existing.setTerminosCondiciones(config.getTerminosCondiciones());
        existing.setMensajeAgradecimiento(config.getMensajeAgradecimiento());
        existing.setFacebookUrl(config.getFacebookUrl());
        existing.setInstagramUrl(config.getInstagramUrl());
        existing.setWhatsapp(config.getWhatsapp());
        existing.setLogoUrl(config.getLogoUrl());
        existing.setLogoBase64(config.getLogoBase64());

        ConfiguracionEmpresa saved = configuracionRepository.save(existing);

        return ResponseEntity.ok(
                ApiResponse.success("Configuración actualizada exitosamente", saved)
        );
    }

    @Operation(
        summary = "Obtener prefijo de ticket",
        description = "Obtiene el prefijo completo para generación de tickets"
    )
    @GetMapping("/prefijo-ticket")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECEPCIONISTA')")
    public ResponseEntity<ApiResponse<String>> obtenerPrefijoTicket() {
        log.info("GET /api/configuracion/prefijo-ticket");

        String prefijo = configuracionRepository.findFirstActiveConfiguration()
                .map(ConfiguracionEmpresa::getPrefijoCompleto)
                .orElse("TES-MAT");

        return ResponseEntity.ok(
                ApiResponse.success("Prefijo obtenido", prefijo)
        );
    }

    @Operation(
        summary = "Verificar si existe configuración",
        description = "Verifica si existe una configuración activa"
    )
    @GetMapping("/existe")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECEPCIONISTA', 'TECNICO')")
    public ResponseEntity<ApiResponse<Boolean>> existeConfiguracion() {
        log.info("GET /api/configuracion/existe");

        boolean existe = configuracionRepository.existsByActivoTrue();

        return ResponseEntity.ok(
                ApiResponse.success("Verificación completada", existe)
        );
    }
}
