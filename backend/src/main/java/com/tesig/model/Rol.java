package com.tesig.model;

public enum Rol {
    ADMINISTRADOR("Administrador", "Acceso total al sistema"),
    TECNICO("Técnico", "Realizar diagnóstico y reparaciones"),
    RECEPCIONISTA("Recepcionista", "Registrar clientes y equipos");

    private final String nombre;
    private final String descripcion;

    Rol(String nombre, String descripcion) {
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
