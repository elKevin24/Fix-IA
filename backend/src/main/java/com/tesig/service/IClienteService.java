package com.tesig.service;

import com.tesig.dto.PaginatedResponseDTO;
import com.tesig.dto.TicketConsultaPublicaDTO;
import com.tesig.dto.cliente.ClienteCreateDTO;
import com.tesig.dto.cliente.ClienteDTO;
import com.tesig.dto.cliente.ClienteUpdateDTO;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Servicio para gestión de clientes.
 *
 * Responsabilidades:
 * - CRUD completo de clientes
 * - Búsqueda y filtrado
 * - Validaciones de negocio
 * - Historial de tickets por cliente
 */
public interface IClienteService {

    /**
     * Obtiene todos los clientes con paginación.
     *
     * @param pageable Configuración de paginación y ordenamiento
     * @return Página de clientes
     */
    PaginatedResponseDTO<ClienteDTO> findAll(Pageable pageable);

    /**
     * Busca un cliente por su ID.
     *
     * @param id ID del cliente
     * @return Cliente encontrado
     * @throws com.tesig.exception.ResourceNotFoundException si no existe
     */
    ClienteDTO findById(Long id);

    /**
     * Crea un nuevo cliente.
     *
     * @param createDTO Datos del cliente a crear
     * @return Cliente creado
     * @throws IllegalArgumentException si el teléfono ya existe
     */
    ClienteDTO create(ClienteCreateDTO createDTO);

    /**
     * Actualiza un cliente existente (actualización completa).
     *
     * @param id ID del cliente
     * @param updateDTO Datos a actualizar
     * @return Cliente actualizado
     * @throws com.tesig.exception.ResourceNotFoundException si no existe
     */
    ClienteDTO update(Long id, ClienteUpdateDTO updateDTO);

    /**
     * Actualiza parcialmente un cliente (solo campos no nulos).
     *
     * @param id ID del cliente
     * @param updateDTO Datos a actualizar
     * @return Cliente actualizado
     * @throws com.tesig.exception.ResourceNotFoundException si no existe
     */
    ClienteDTO partialUpdate(Long id, ClienteUpdateDTO updateDTO);

    /**
     * Elimina un cliente (soft delete).
     *
     * @param id ID del cliente
     * @throws com.tesig.exception.ResourceNotFoundException si no existe
     */
    void delete(Long id);

    /**
     * Obtiene el historial de tickets de un cliente.
     *
     * @param id ID del cliente
     * @return Lista de tickets del cliente
     * @throws com.tesig.exception.ResourceNotFoundException si no existe
     */
    List<TicketConsultaPublicaDTO> getTicketsByCliente(Long id);

    /**
     * Busca clientes por nombre, apellido o teléfono.
     *
     * @param query Texto a buscar
     * @return Lista de clientes que coinciden
     */
    List<ClienteDTO> search(String query);

    /**
     * Verifica si un teléfono ya está registrado.
     *
     * @param telefono Teléfono a verificar
     * @param excludeId ID del cliente a excluir (para updates)
     * @return true si existe, false si no
     */
    boolean existsByTelefono(String telefono, Long excludeId);
}
