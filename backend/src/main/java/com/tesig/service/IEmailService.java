package com.tesig.service;

import com.tesig.model.Ticket;

/**
 * Interface para el servicio de envío de emails.
 *
 * Aplicación de principios SOLID:
 * - Single Responsibility: Solo gestiona envío de emails
 * - Open/Closed: Fácil agregar nuevos tipos de emails
 * - Dependency Inversion: Clientes dependen de esta abstracción
 *
 * Tipos de notificaciones por email:
 * 1. Ticket creado (con enlace de consulta)
 * 2. Presupuesto disponible (diagnóstico listo)
 * 3. Equipo listo para entrega
 * 4. Recordatorio de recogida
 */
public interface IEmailService {

    /**
     * Envía email cuando se crea un nuevo ticket.
     *
     * Contenido del email:
     * - Número de ticket
     * - Enlace para consultar estado
     * - Información del equipo
     *
     * @param ticket Ticket recién creado
     */
    void enviarEmailTicketCreado(Ticket ticket);

    /**
     * Envía email cuando el presupuesto está disponible.
     *
     * Contenido del email:
     * - Diagnóstico del problema
     * - Presupuesto detallado
     * - Tiempo estimado
     * - Instrucciones para aprobar/rechazar
     *
     * @param ticket Ticket con presupuesto
     */
    void enviarEmailPresupuestoDisponible(Ticket ticket);

    /**
     * Envía email cuando el equipo está listo para entrega.
     *
     * Contenido del email:
     * - Aviso de que ya puede recoger su equipo
     * - Horario de atención
     * - Número de ticket para presentar
     *
     * @param ticket Ticket listo para entrega
     */
    void enviarEmailListoParaEntrega(Ticket ticket);

    /**
     * Envía email de recordatorio cuando han pasado varios días
     * y el cliente no ha recogido su equipo.
     *
     * @param ticket Ticket pendiente de recogida
     * @param diasEspera Días que lleva esperando
     */
    void enviarEmailRecordatorioRecogida(Ticket ticket, int diasEspera);

    /**
     * Envía un email genérico.
     *
     * @param destinatario Email del destinatario
     * @param asunto Asunto del email
     * @param contenido Contenido en HTML
     */
    void enviarEmail(String destinatario, String asunto, String contenido);

    /**
     * Verifica si el servicio de email está habilitado.
     *
     * @return true si está habilitado
     */
    boolean isEmailEnabled();
}
