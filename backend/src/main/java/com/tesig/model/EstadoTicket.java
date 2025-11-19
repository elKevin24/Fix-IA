package com.tesig.model;

public enum EstadoTicket {
    INGRESADO("Ingresado", "Equipo recibido, esperando asignación"),
    EN_DIAGNOSTICO("En Diagnóstico", "Técnico está evaluando el equipo"),
    PRESUPUESTADO("Presupuestado", "Esperando respuesta del cliente"),
    APROBADO("Aprobado", "Cliente aceptó el presupuesto"),
    RECHAZADO("Rechazado", "Cliente rechazó el presupuesto"),
    EN_REPARACION("En Reparación", "Reparación en proceso"),
    EN_PRUEBA("En Prueba", "Validando funcionamiento"),
    LISTO_ENTREGA("Listo para Entrega", "Equipo reparado, esperando cliente"),
    ENTREGADO("Entregado", "Equipo entregado al cliente"),
    CANCELADO("Cancelado", "Ticket cerrado sin completar servicio");

    private final String nombre;
    private final String descripcion;

    EstadoTicket(String nombre, String descripcion) {
        this.nombre = nombre;
        this.descripcion = descripcion;
    }

    public String getNombre() {
        return nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }
}
