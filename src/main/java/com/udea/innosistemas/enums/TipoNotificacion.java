package com.udea.innosistemas.enums;

/**
 * Enum que define los tipos de notificaciones en el sistema
 * Utilizado para categorizar las notificaciones que reciben los usuarios
 *
 * Autor: Fábrica-Escuela de Software UdeA
 * Versión: 1.0.0
 */
public enum TipoNotificacion {
    /**
     * Notificaciones relacionadas con equipos
     */
    EQUIPO("Notificación de equipo"),

    /**
     * Notificaciones de invitaciones
     */
    INVITACION("Invitación"),

    /**
     * Notificaciones de alertas importantes
     */
    ALERTA("Alerta"),

    /**
     * Notificaciones informativas
     */
    INFO("Información"),

    /**
     * Notificaciones de recordatorios
     */
    RECORDATORIO("Recordatorio"),

    /**
     * Notificaciones de tareas
     */
    TAREA("Tarea"),

    /**
     * Notificaciones de sistema
     */
    SISTEMA("Sistema"),

    /**
     * Notificaciones de fechas límite
     */
    FECHA_LIMITE("Fecha límite");

    private final String descripcion;

    TipoNotificacion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }
}
