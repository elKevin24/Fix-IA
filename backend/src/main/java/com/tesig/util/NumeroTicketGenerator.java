package com.tesig.util;

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
 * - Dependency Inversion: Depende de la abstracción (TicketRepository)
 *
 * Pattern: Factory Method Pattern
 *
 * Formato: TKT-YYYYMMDD-NNNN
 * Ejemplo: TKT-20251105-0001
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class NumeroTicketGenerator {

    private final TicketRepository ticketRepository;

    private static final String PREFIX = "TKT";
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd");
    private static final int SEQUENCE_LENGTH = 4;

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

        // Buscar el último ticket del día
        int sequence = getNextSequence(datePart);

        String numeroTicket = buildNumeroTicket(datePart, sequence);

        // Verificar que no exista (por seguridad)
        while (ticketRepository.existsByNumeroTicket(numeroTicket)) {
            log.warn("Número de ticket duplicado detectado: {}, generando nuevo", numeroTicket);
            sequence++;
            numeroTicket = buildNumeroTicket(datePart, sequence);
        }

        log.debug("Número de ticket generado: {}", numeroTicket);
        return numeroTicket;
    }

    /**
     * Obtiene el siguiente número de secuencia para el día.
     *
     * @param datePart Parte de fecha en formato yyyyMMdd
     * @return Siguiente número de secuencia
     */
    private int getNextSequence(String datePart) {
        String pattern = PREFIX + "-" + datePart + "%";

        // Buscar todos los tickets del día y obtener el máximo
        // Este query busca el último número de secuencia usado
        long count = ticketRepository.count();

        // Por simplicidad, usar un contador basado en fecha
        // En producción real, considera usar una tabla de secuencias
        String startOfDay = PREFIX + "-" + datePart + "-0000";
        String endOfDay = PREFIX + "-" + datePart + "-9999";

        // Contar tickets del día
        // Nota: Este es un approach simplificado. Para alta concurrencia,
        // considera usar una tabla de secuencias con locks optimistas

        return 1; // Por ahora retorna 1, se validará con existsByNumeroTicket
    }

    /**
     * Construye el número de ticket con formato.
     *
     * @param datePart Fecha en formato yyyyMMdd
     * @param sequence Número de secuencia
     * @return Número de ticket formateado
     */
    private String buildNumeroTicket(String datePart, int sequence) {
        String sequenceStr = String.format("%0" + SEQUENCE_LENGTH + "d", sequence);
        return PREFIX + "-" + datePart + "-" + sequenceStr;
    }

    /**
     * Valida el formato de un número de ticket.
     *
     * @param numeroTicket Número a validar
     * @return true si el formato es válido
     */
    public boolean isValidFormat(String numeroTicket) {
        if (numeroTicket == null || numeroTicket.isBlank()) {
            return false;
        }

        // Validar formato: TKT-YYYYMMDD-NNNN
        String regex = "^TKT-\\d{8}-\\d{4}$";
        return numeroTicket.matches(regex);
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

        // Formato: TKT-YYYYMMDD-NNNN
        // Posición: 0123456789...
        return numeroTicket.substring(4, 12);
    }
}
