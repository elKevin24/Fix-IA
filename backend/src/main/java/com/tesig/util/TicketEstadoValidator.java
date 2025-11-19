package com.tesig.util;

import com.tesig.model.EstadoTicket;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

/**
 * Validador de transiciones de estado de tickets.
 *
 * Aplicación de principios SOLID:
 * - Single Responsibility: Solo valida transiciones de estado
 * - Open/Closed: Fácil agregar nuevas reglas sin modificar código existente
 * - Liskov Substitution: Puede ser extendido si se necesita
 *
 * Pattern: Strategy Pattern para reglas de negocio
 */
@Component
public class TicketEstadoValidator {

    // Mapa de transiciones válidas por estado
    // Dependency Inversion: Depende de la abstracción (enum) no de implementaciones concretas
    private final Map<EstadoTicket, Set<EstadoTicket>> transicionesValidas;

    public TicketEstadoValidator() {
        transicionesValidas = new EnumMap<>(EstadoTicket.class);
        inicializarTransiciones();
    }

    /**
     * Valida si una transición de estado es permitida.
     *
     * @param estadoActual Estado actual del ticket
     * @param estadoNuevo Estado al que se quiere transicionar
     * @return true si la transición es válida, false en caso contrario
     */
    public boolean esTransicionValida(EstadoTicket estadoActual, EstadoTicket estadoNuevo) {
        if (estadoActual == null || estadoNuevo == null) {
            return false;
        }

        // Mismo estado siempre es válido
        if (estadoActual == estadoNuevo) {
            return true;
        }

        // CANCELADO se puede hacer desde cualquier estado (excepto ENTREGADO)
        if (estadoNuevo == EstadoTicket.CANCELADO && estadoActual != EstadoTicket.ENTREGADO) {
            return true;
        }

        // Verificar si la transición está en el mapa
        Set<EstadoTicket> estadosPermitidos = transicionesValidas.get(estadoActual);
        return estadosPermitidos != null && estadosPermitidos.contains(estadoNuevo);
    }

    /**
     * Obtiene un mensaje descriptivo de error para una transición inválida.
     *
     * @param estadoActual Estado actual
     * @param estadoNuevo Estado deseado
     * @return Mensaje de error descriptivo
     */
    public String getMensajeErrorTransicion(EstadoTicket estadoActual, EstadoTicket estadoNuevo) {
        return String.format(
                "Transición de estado inválida: No se puede cambiar de '%s' a '%s'. " +
                        "Transiciones permitidas desde '%s': %s",
                estadoActual.getNombre(),
                estadoNuevo.getNombre(),
                estadoActual.getNombre(),
                obtenerEstadosPermitidosString(estadoActual)
        );
    }

    /**
     * Obtiene los estados a los que se puede transicionar desde el estado actual.
     *
     * @param estadoActual Estado actual
     * @return Set de estados permitidos
     */
    public Set<EstadoTicket> getEstadosPermitidos(EstadoTicket estadoActual) {
        Set<EstadoTicket> permitidos = EnumSet.noneOf(EstadoTicket.class);

        // Agregar transiciones normales
        Set<EstadoTicket> transiciones = transicionesValidas.get(estadoActual);
        if (transiciones != null) {
            permitidos.addAll(transiciones);
        }

        // CANCELADO siempre es posible (excepto desde ENTREGADO)
        if (estadoActual != EstadoTicket.ENTREGADO && estadoActual != EstadoTicket.CANCELADO) {
            permitidos.add(EstadoTicket.CANCELADO);
        }

        return permitidos;
    }

    /**
     * Inicializa el mapa de transiciones válidas.
     * Este método define las reglas de negocio para transiciones de estado.
     */
    private void inicializarTransiciones() {
        // INGRESADO → puede ir a EN_DIAGNOSTICO
        transicionesValidas.put(
                EstadoTicket.INGRESADO,
                EnumSet.of(EstadoTicket.EN_DIAGNOSTICO)
        );

        // EN_DIAGNOSTICO → puede ir a PRESUPUESTADO
        transicionesValidas.put(
                EstadoTicket.EN_DIAGNOSTICO,
                EnumSet.of(EstadoTicket.PRESUPUESTADO)
        );

        // PRESUPUESTADO → puede ir a APROBADO o RECHAZADO
        transicionesValidas.put(
                EstadoTicket.PRESUPUESTADO,
                EnumSet.of(EstadoTicket.APROBADO, EstadoTicket.RECHAZADO)
        );

        // APROBADO → puede ir a EN_REPARACION
        transicionesValidas.put(
                EstadoTicket.APROBADO,
                EnumSet.of(EstadoTicket.EN_REPARACION)
        );

        // RECHAZADO → solo puede ir a CANCELADO
        transicionesValidas.put(
                EstadoTicket.RECHAZADO,
                EnumSet.of(EstadoTicket.CANCELADO)
        );

        // EN_REPARACION → puede ir a EN_PRUEBA
        transicionesValidas.put(
                EstadoTicket.EN_REPARACION,
                EnumSet.of(EstadoTicket.EN_PRUEBA)
        );

        // EN_PRUEBA → puede ir a LISTO_ENTREGA o volver a EN_REPARACION
        transicionesValidas.put(
                EstadoTicket.EN_PRUEBA,
                EnumSet.of(EstadoTicket.LISTO_ENTREGA, EstadoTicket.EN_REPARACION)
        );

        // LISTO_ENTREGA → puede ir a ENTREGADO
        transicionesValidas.put(
                EstadoTicket.LISTO_ENTREGA,
                EnumSet.of(EstadoTicket.ENTREGADO)
        );

        // ENTREGADO y CANCELADO son estados finales (no pueden transicionar)
        transicionesValidas.put(EstadoTicket.ENTREGADO, EnumSet.noneOf(EstadoTicket.class));
        transicionesValidas.put(EstadoTicket.CANCELADO, EnumSet.noneOf(EstadoTicket.class));
    }

    /**
     * Helper method para obtener string de estados permitidos.
     */
    private String obtenerEstadosPermitidosString(EstadoTicket estado) {
        Set<EstadoTicket> permitidos = getEstadosPermitidos(estado);
        if (permitidos.isEmpty()) {
            return "Ninguno (estado final)";
        }

        StringBuilder sb = new StringBuilder();
        permitidos.forEach(e -> {
            if (sb.length() > 0) sb.append(", ");
            sb.append(e.getNombre());
        });
        return sb.toString();
    }
}
