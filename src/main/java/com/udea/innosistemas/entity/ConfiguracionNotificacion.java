package com.udea.innosistemas.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "configuracion_notificacion")
public class ConfiguracionNotificacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idConfiguracion;

    private Long idUsuario;
    private Boolean recibirCorreos = true;
    private Boolean notificacionesTareas = true;
    private Boolean notificacionesEquipos = true;
    private Boolean notificacionesSistema = true;
    private Boolean alertasVencimiento = true;
    private String frecuenciaCorreos = "inmediato";

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Long getIdConfiguracion() {
        return idConfiguracion;
    }

    public void setIdConfiguracion(Long idConfiguracion) {
        this.idConfiguracion = idConfiguracion;
    }

    public Long getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Long idUsuario) {
        this.idUsuario = idUsuario;
    }

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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
