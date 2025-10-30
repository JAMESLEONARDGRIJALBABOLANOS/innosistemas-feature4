package com.udea.innosistemas.event;

import com.udea.innosistemas.enums.TipoEvento;
import org.springframework.context.ApplicationEvent;

import java.time.LocalDateTime;

/**
 * Evento de dominio base para eventos de equipos
 * Representa un evento que ocurre en el contexto de un equipo
 * Utiliza el patrón Observer de Spring para publicar eventos
 *
 * Autor: Fábrica-Escuela de Software UdeA
 * Versión: 1.0.0
 */
public class TeamEvent extends ApplicationEvent {

    private final Long teamId;
    private final TipoEvento tipoEvento;
    private final Long usuarioOrigenId;
    private final String detalles;
    private final LocalDateTime timestamp;
    private final String metadata;

    public TeamEvent(Object source, Long teamId, TipoEvento tipoEvento, Long usuarioOrigenId, String detalles) {
        super(source);
        this.teamId = teamId;
        this.tipoEvento = tipoEvento;
        this.usuarioOrigenId = usuarioOrigenId;
        this.detalles = detalles;
        this.timestamp = LocalDateTime.now();
        this.metadata = null;
    }

    public TeamEvent(Object source, Long teamId, TipoEvento tipoEvento, Long usuarioOrigenId, String detalles, String metadata) {
        super(source);
        this.teamId = teamId;
        this.tipoEvento = tipoEvento;
        this.usuarioOrigenId = usuarioOrigenId;
        this.detalles = detalles;
        this.timestamp = LocalDateTime.now();
        this.metadata = metadata;
    }

    public Long getTeamId() {
        return teamId;
    }

    public TipoEvento getTipoEvento() {
        return tipoEvento;
    }

    public Long getUsuarioOrigenId() {
        return usuarioOrigenId;
    }

    public String getDetalles() {
        return detalles;
    }

    public LocalDateTime getEventTimestamp() {
        return timestamp;
    }

    public String getMetadata() {
        return metadata;
    }

    @Override
    public String toString() {
        return "TeamEvent{" +
                "teamId=" + teamId +
                ", tipoEvento=" + tipoEvento +
                ", usuarioOrigenId=" + usuarioOrigenId +
                ", detalles='" + detalles + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}
