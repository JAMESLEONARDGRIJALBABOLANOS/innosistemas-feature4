package com.udea.innosistemas.enums;

/**
 * Enum que define los tipos de eventos relacionados con equipos
 * Utilizado para categorizar las diferentes acciones que pueden ocurrir en un equipo
 *
 * Autor: Fábrica-Escuela de Software UdeA
 * Versión: 1.0.0
 */
public enum TipoEvento {
    /**
     * Evento cuando se crea un nuevo equipo
     */
    CREACION_EQUIPO("Se ha creado un nuevo equipo"),

    /**
     * Evento cuando un usuario es invitado a unirse al equipo
     */
    INVITACION_EQUIPO("Has sido invitado a unirte a un equipo"),

    /**
     * Evento cuando un usuario se une al equipo
     */
    MIEMBRO_UNIDO("Un nuevo miembro se ha unido al equipo"),

    /**
     * Evento cuando un usuario abandona el equipo
     */
    MIEMBRO_ABANDONA("Un miembro ha abandonado el equipo"),

    /**
     * Evento cuando se actualiza la fecha límite del equipo
     */
    FECHA_LIMITE_ACTUALIZADA("La fecha límite del equipo ha sido actualizada"),

    /**
     * Evento cuando se aproxima la fecha límite
     */
    FECHA_LIMITE_PROXIMA("La fecha límite del equipo está próxima"),

    /**
     * Evento cuando se alcanza la fecha límite
     */
    FECHA_LIMITE_ALCANZADA("Se ha alcanzado la fecha límite del equipo"),

    /**
     * Evento cuando se actualiza información del equipo
     */
    EQUIPO_ACTUALIZADO("La información del equipo ha sido actualizada"),

    /**
     * Evento cuando se elimina el equipo
     */
    EQUIPO_ELIMINADO("El equipo ha sido eliminado"),

    /**
     * Evento cuando se cambia el líder del equipo
     */
    CAMBIO_LIDER("Ha cambiado el líder del equipo"),

    /**
     * Evento cuando se alcanza el límite de miembros
     */
    LIMITE_MIEMBROS_ALCANZADO("El equipo ha alcanzado su límite de miembros"),

    /**
     * Evento cuando se asigna una tarea al equipo
     */
    TAREA_ASIGNADA("Se ha asignado una nueva tarea al equipo"),

    /**
     * Evento cuando se completa una tarea del equipo
     */
    TAREA_COMPLETADA("Se ha completado una tarea del equipo");

    private final String descripcionPredeterminada;

    TipoEvento(String descripcionPredeterminada) {
        this.descripcionPredeterminada = descripcionPredeterminada;
    }

    public String getDescripcionPredeterminada() {
        return descripcionPredeterminada;
    }

    /**
     * Verifica si el evento es crítico y requiere notificación inmediata
     */
    public boolean esCritico() {
        return this == FECHA_LIMITE_ALCANZADA ||
               this == EQUIPO_ELIMINADO ||
               this == FECHA_LIMITE_PROXIMA;
    }

    /**
     * Verifica si el evento debe notificar a todos los miembros del equipo
     */
    public boolean notificarATodos() {
        return this != INVITACION_EQUIPO; // La invitación solo va al usuario invitado
    }
}
