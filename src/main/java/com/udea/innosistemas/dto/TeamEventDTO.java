package com.udea.innosistemas.dto;

import com.udea.innosistemas.enums.TipoEvento;

import java.time.LocalDateTime;

/**
 * DTO para representar eventos de equipos
 * Contiene información sobre eventos que ocurren en los equipos
 *
 * Autor: Fábrica-Escuela de Software UdeA
 * Versión: 1.0.0
 */
public class TeamEventDTO {

    private Long teamId;
    private TipoEvento tipoEvento;
    private Long usuarioOrigenId;
    private String detalles;
    private LocalDateTime timestamp;
    private String metadata;

    public TeamEventDTO() {
        this.timestamp = LocalDateTime.now();
    }

    public TeamEventDTO(Long teamId, TipoEvento tipoEvento, Long usuarioOrigenId, String detalles) {
        this.teamId = teamId;
        this.tipoEvento = tipoEvento;
        this.usuarioOrigenId = usuarioOrigenId;
        this.detalles = detalles;
        this.timestamp = LocalDateTime.now();
    }

    // Getters y Setters
    public Long getTeamId() {
        return teamId;
    }

    public void setTeamId(Long teamId) {
        this.teamId = teamId;
    }

    public TipoEvento getTipoEvento() {
        return tipoEvento;
    }

    public void setTipoEvento(TipoEvento tipoEvento) {
        this.tipoEvento = tipoEvento;
    }

    public Long getUsuarioOrigenId() {
        return usuarioOrigenId;
    }

    public void setUsuarioOrigenId(Long usuarioOrigenId) {
        this.usuarioOrigenId = usuarioOrigenId;
    }

    public String getDetalles() {
        return detalles;
    }

    public void setDetalles(String detalles) {
        this.detalles = detalles;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public String getMetadata() {
        return metadata;
    }

    public void setMetadata(String metadata) {
        this.metadata = metadata;
    }
}
