package com.udea.innosistemas.dto;

/**
 * DTO de entrada para la configuración de notificaciones del usuario.
 * Este objeto se recibe desde GraphQL.
 */
public class ConfigInput {

    private Boolean recibirCorreos;
    private Boolean notificacionesTareas;
    private Boolean notificacionesEquipos;
    private Boolean notificacionesSistema;
    private Boolean alertasVencimiento;
    private String frecuenciaCorreos;

    // Getters y setters necesarios para que NotificationService funcione
    public Boolean getRecibirCorreos() {
        return recibirCorreos;
    }

    public void setRecibirCorreos(Boolean recibirCorreos) {
        this.recibirCorreos = recibirCorreos;
    }

    public Boolean getNotificacionesTareas() {
        return notificacionesTareas;
    }

    public void setNotificacionesTareas(Boolean notificacionesTareas) {
        this.notificacionesTareas = notificacionesTareas;
    }

    public Boolean getNotificacionesEquipos() {
        return notificacionesEquipos;
    }

    public void setNotificacionesEquipos(Boolean notificacionesEquipos) {
        this.notificacionesEquipos = notificacionesEquipos;
    }

    public Boolean getNotificacionesSistema() {
        return notificacionesSistema;
    }

    public void setNotificacionesSistema(Boolean notificacionesSistema) {
        this.notificacionesSistema = notificacionesSistema;
    }

    public Boolean getAlertasVencimiento() {
        return alertasVencimiento;
    }

    public void setAlertasVencimiento(Boolean alertasVencimiento) {
        this.alertasVencimiento = alertasVencimiento;
    }

    public String getFrecuenciaCorreos() {
        return frecuenciaCorreos;
    }

    public void setFrecuenciaCorreos(String frecuenciaCorreos) {
        this.frecuenciaCorreos = frecuenciaCorreos;
    }
}
