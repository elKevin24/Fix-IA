package com.tesig.service;

import com.tesig.model.Ticket;

/**
 * Interface para el servicio de generación de documentos PDF.
 *
 * Aplicación de principios SOLID:
 * - Single Responsibility: Solo genera documentos PDF
 * - Open/Closed: Fácil agregar nuevos tipos de PDFs
 * - Dependency Inversion: Clientes dependen de esta abstracción
 */
public interface IPDFService {

    /**
     * Genera un PDF del ticket para imprimir y entregar al cliente.
     *
     * El PDF incluye:
     * - Información del taller
     * - Número de ticket grande y destacado
     * - Código QR para consulta rápida
     * - Información del equipo
     * - Información del cliente
     * - Falla reportada
     * - Fecha de ingreso
     * - Instrucciones de consulta
     *
     * @param ticket Ticket a imprimir
     * @return Array de bytes del PDF
     */
    byte[] generarTicketPDF(Ticket ticket);

    /**
     * Genera un PDF con el presupuesto detallado.
     *
     * @param ticket Ticket con presupuesto
     * @return Array de bytes del PDF
     */
    byte[] generarPresupuestoPDF(Ticket ticket);

    /**
     * Genera un PDF del comprobante de entrega exitosa.
     *
     * El PDF incluye:
     * - Información del taller
     * - Número de ticket
     * - Resumen de trabajo realizado
     * - Piezas utilizadas
     * - Detalle de costos
     * - Descuentos aplicados
     * - Total final
     * - Información de garantía
     * - Firma del cliente
     *
     * @param ticket Ticket entregado
     * @return Array de bytes del PDF
     */
    byte[] generarComprobanteEntregaPDF(Ticket ticket);
}
