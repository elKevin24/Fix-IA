package com.tesig.service;

import com.tesig.dto.AgregarPiezaTicketDTO;
import com.tesig.dto.TicketPiezaResponseDTO;

import java.math.BigDecimal;
import java.util.List;

/**
 * Interfaz del servicio para gestión de piezas asociadas a tickets.
 *
 * Aplicación de principios SOLID:
 * - Dependency Inversion: Define abstracción para la lógica de negocio
 * - Single Responsibility: Solo gestiona la relación ticket-pieza
 *
 * @author TESIG System
 */
public interface ITicketPiezaService {

    /**
     * Agrega una pieza a un ticket
     * Valida stock disponible y descuenta automáticamente
     */
    TicketPiezaResponseDTO agregarPiezaATicket(Long ticketId, AgregarPiezaTicketDTO dto);

    /**
     * Remueve una pieza de un ticket
     * Reintegra el stock si ya fue descontado
     */
    void removerPiezaDeTicket(Long ticketPiezaId);

    /**
     * Actualiza la cantidad de una pieza en un ticket
     */
    TicketPiezaResponseDTO actualizarCantidad(Long ticketPiezaId, Integer nuevaCantidad);

    /**
     * Obtiene todas las piezas de un ticket
     */
    List<TicketPiezaResponseDTO> obtenerPiezasDeTicket(Long ticketId);

    /**
     * Calcula el total de las piezas de un ticket
     */
    BigDecimal calcularTotalPiezas(Long ticketId);

    /**
     * Descuenta todas las piezas pendientes de un ticket del inventario
     * Se llama cuando se aprueba el presupuesto
     */
    void descontarPiezasDelInventario(Long ticketId);

    /**
     * Reintegra todas las piezas de un ticket al inventario
     * Se llama cuando se cancela o rechaza un ticket
     */
    void reintegrarPiezasAlInventario(Long ticketId);
}
