package com.tesig.util;

import com.tesig.model.ConfiguracionEmpresa;
import com.tesig.repository.ConfiguracionEmpresaRepository;
import com.tesig.repository.TicketRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Generador de números únicos para tickets.
 *
 * Aplicación de principios SOLID:
 * - Single Responsibility: Solo genera números de ticket
 * - Dependency Inversion: Depende de abstracciones (Repositories)
 *
 * Pattern: Factory Method Pattern
 *
 * Formato escalable: {EMPRESA}-{SUCURSAL}-{YYYYMMDD}-{NNNN}
 * Ejemplo: TES-MAT-20251118-0001
 *
 * Esto permite:
 * - Identificar la empresa/taller de origen
 * - Identificar la sucursal
 * - Escalar a múltiples talleres
 * - Mantener trazabilidad completa
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class NumeroTicketGenerator {

    private final TicketRepository ticketRepository;
    private final ConfiguracionEmpresaRepository configuracionRepository;

    private static final String DEFAULT_EMPRESA = "TES";
    private static final String DEFAULT_SUCURSAL = "MAT";
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd");
    private static final int DEFAULT_SEQUENCE_LENGTH = 4;

    /**
     * Genera un número de ticket único.
     *
     * Thread-safe mediante sincronización en el método.
     *
     * @return Número de ticket único
     */
    public synchronized String generate() {
        LocalDateTime now = LocalDateTime.now();
        String datePart = now.format(DATE_FORMAT);

        // Obtener configuración de empresa
        ConfiguracionEmpresa config = getConfiguracion();
        String prefix = buildPrefix(config);
        int sequenceLength = config != null && config.getLongitudSecuencia() != null
                ? config.getLongitudSecuencia()
                : DEFAULT_SEQUENCE_LENGTH;

        // Buscar el último ticket del día
        int sequence = getNextSequence(prefix, datePart);

        String numeroTicket = buildNumeroTicket(prefix, datePart, sequence, sequenceLength);

        // Verificar que no exista (por seguridad)
        while (ticketRepository.existsByNumeroTicket(numeroTicket)) {
            log.warn("Número de ticket duplicado detectado: {}, generando nuevo", numeroTicket);
            sequence++;
            numeroTicket = buildNumeroTicket(prefix, datePart, sequence, sequenceLength);
        }

        log.info("Número de ticket generado: {}", numeroTicket);
        return numeroTicket;
    }

    /**
     * Obtiene la configuración activa de la empresa.
     *
     * @return ConfiguracionEmpresa o null si no existe
     */
    private ConfiguracionEmpresa getConfiguracion() {
        return configuracionRepository.findFirstActiveConfiguration().orElse(null);
    }

    /**
     * Construye el prefijo del ticket basado en la configuración.
     *
     * @param config Configuración de empresa
     * @return Prefijo (ej: TES-MAT)
     */
    private String buildPrefix(ConfiguracionEmpresa config) {
        if (config != null) {
            return config.getPrefijoCompleto();
        }
        return DEFAULT_EMPRESA + "-" + DEFAULT_SUCURSAL;
    }

    /**
     * Obtiene el siguiente número de secuencia para el día.
     *
     * @param prefix Prefijo del ticket
     * @param datePart Parte de fecha en formato yyyyMMdd
     * @return Siguiente número de secuencia
     */
    private int getNextSequence(String prefix, String datePart) {
        // Buscar el último número de secuencia del día
        String pattern = prefix + "-" + datePart + "-%";

        // Contar tickets existentes con este patrón y sumar 1
        // Este es un approach simplificado que funciona con existsByNumeroTicket
        // Para alta concurrencia, considera usar una tabla de secuencias con locks optimistas

        return 1;
    }

    /**
     * Construye el número de ticket con formato.
     *
     * @param prefix Prefijo de empresa-sucursal
     * @param datePart Fecha en formato yyyyMMdd
     * @param sequence Número de secuencia
     * @param sequenceLength Longitud del número de secuencia
     * @return Número de ticket formateado
     */
    private String buildNumeroTicket(String prefix, String datePart, int sequence, int sequenceLength) {
        String sequenceStr = String.format("%0" + sequenceLength + "d", sequence);
        return prefix + "-" + datePart + "-" + sequenceStr;
    }

    /**
     * Valida el formato de un número de ticket.
     * Soporta tanto el formato antiguo (TKT-) como el nuevo (EMPRESA-SUCURSAL-)
     *
     * @param numeroTicket Número a validar
     * @return true si el formato es válido
     */
    public boolean isValidFormat(String numeroTicket) {
        if (numeroTicket == null || numeroTicket.isBlank()) {
            return false;
        }

        // Formato antiguo: TKT-YYYYMMDD-NNNN
        // Formato nuevo: XXX-XXX-YYYYMMDD-NNNN o XXX-XXXX-YYYYMMDD-NNNN
        String regexOld = "^TKT-\\d{8}-\\d{4}$";
        String regexNew = "^[A-Z]{2,5}-[A-Z0-9]{2,4}-\\d{8}-\\d{4,6}$";

        return numeroTicket.matches(regexOld) || numeroTicket.matches(regexNew);
    }

    /**
     * Extrae la fecha de un número de ticket.
     *
     * @param numeroTicket Número de ticket
     * @return Fecha en formato yyyyMMdd
     */
    public String extractDate(String numeroTicket) {
        if (!isValidFormat(numeroTicket)) {
            throw new IllegalArgumentException("Formato de número de ticket inválido");
        }

        // Buscar la parte de fecha (8 dígitos consecutivos)
        String[] parts = numeroTicket.split("-");
        for (String part : parts) {
            if (part.matches("\\d{8}")) {
                return part;
            }
        }

        throw new IllegalArgumentException("No se pudo extraer la fecha del número de ticket");
    }

    /**
     * Extrae el código de empresa del número de ticket.
     *
     * @param numeroTicket Número de ticket
     * @return Código de empresa
     */
    public String extractCodigoEmpresa(String numeroTicket) {
        if (numeroTicket == null || numeroTicket.isBlank()) {
            return null;
        }

        String[] parts = numeroTicket.split("-");
        if (parts.length >= 1) {
            return parts[0];
        }

        return null;
    }

    /**
     * Extrae el código de sucursal del número de ticket.
     *
     * @param numeroTicket Número de ticket
     * @return Código de sucursal
     */
    public String extractCodigoSucursal(String numeroTicket) {
        if (numeroTicket == null || numeroTicket.isBlank()) {
            return null;
        }

        String[] parts = numeroTicket.split("-");
        if (parts.length >= 2 && !parts[1].matches("\\d{8}")) {
            return parts[1];
        }

        return null;
    }

    /**
     * Genera el formato de visualización del número de ticket.
     * Útil para mostrar en documentos impresos.
     *
     * @param numeroTicket Número de ticket
     * @return Formato con espacios para mejor legibilidad
     */
    public String formatForDisplay(String numeroTicket) {
        if (numeroTicket == null) return "";

        // Agregar espacios entre las partes principales para mejor legibilidad
        // TES-MAT-20251118-0001 -> TES MAT 20251118 0001
        return numeroTicket.replace("-", " ");
    }
}
