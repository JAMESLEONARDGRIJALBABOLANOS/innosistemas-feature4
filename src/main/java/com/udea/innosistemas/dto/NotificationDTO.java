package com.udea.innosistemas.dto;

import com.udea.innosistemas.entity.Notification;

import java.time.LocalDateTime;

/**
 * DTO para transferir información de notificaciones
 * Contiene los datos esenciales de una notificación para ser enviados al cliente
 *
 * Autor: Fábrica-Escuela de Software UdeA
 * Versión: 1.0.0
 */
public class NotificationDTO {

    private Long id;
    private Long userId;
    private String mensaje;
    private String tipo;
    private boolean leida;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaLectura;
    private Long teamId;
    private Long cursoId;
    private String prioridad;
    private String enlace;
    private String metadata;
    private LocalDateTime expiraEn;

    public NotificationDTO() {
    }

    public NotificationDTO(Notification notification) {
        this.id = notification.getId();
        this.userId = notification.getUserId();
        this.mensaje = notification.getMensaje();
        this.tipo = notification.getTipo();
        this.leida = notification.isLeida();
        this.fechaCreacion = notification.getFechaCreacion();
        this.fechaLectura = notification.getFechaLectura();
        this.teamId = notification.getTeamId();
        this.cursoId = notification.getCursoId();
        this.prioridad = notification.getPrioridad() != null ? notification.getPrioridad().name() : null;
        this.enlace = notification.getEnlace();
        this.metadata = notification.getMetadata();
        this.expiraEn = notification.getExpiraEn();
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public boolean isLeida() {
        return leida;
    }

    public void setLeida(boolean leida) {
        this.leida = leida;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public LocalDateTime getFechaLectura() {
        return fechaLectura;
    }

    public void setFechaLectura(LocalDateTime fechaLectura) {
        this.fechaLectura = fechaLectura;
    }

    public Long getTeamId() {
        return teamId;
    }

    public void setTeamId(Long teamId) {
        this.teamId = teamId;
    }

    public Long getCursoId() {
        return cursoId;
    }

    public void setCursoId(Long cursoId) {
        this.cursoId = cursoId;
    }

    public String getPrioridad() {
        return prioridad;
    }

    public void setPrioridad(String prioridad) {
        this.prioridad = prioridad;
    }

    public String getEnlace() {
        return enlace;
    }

    public void setEnlace(String enlace) {
        this.enlace = enlace;
    }

    public String getMetadata() {
        return metadata;
    }

    public void setMetadata(String metadata) {
        this.metadata = metadata;
    }

    public LocalDateTime getExpiraEn() {
        return expiraEn;
    }

    public void setExpiraEn(LocalDateTime expiraEn) {
        this.expiraEn = expiraEn;
    }

    // Métodos adicionales para compatibilidad con GraphQL schema
    public boolean getLeido() {
        return leida;
    }

    public String getCreatedAt() {
        return fechaCreacion != null ? fechaCreacion.toString() : null;
    }
}
