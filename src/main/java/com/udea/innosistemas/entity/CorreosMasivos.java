package com.udea.innosistemas.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "correos_masivos")
public class CorreosMasivos {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idCorreo;

    private String asunto;

    @Column(columnDefinition = "TEXT")
    private String mensaje;

    private LocalDateTime fecha;
    private String estado;
    private Long idUsuario;
    private String tipoDestinatario;
    private Long idEquipo;
    private LocalDateTime programarEnvio;
    private Integer totalDestinatarios;
    private Integer exitosos;
    private Integer fallidos;

    public Long getIdCorreo() {
        return idCorreo;
    }

    public void setIdCorreo(Long idCorreo) {
        this.idCorreo = idCorreo;
    }

    public String getAsunto() {
        return asunto;
    }

    public void setAsunto(String asunto) {
        this.asunto = asunto;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public Long getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Long idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getTipoDestinatario() {
        return tipoDestinatario;
    }

    public void setTipoDestinatario(String tipoDestinatario) {
        this.tipoDestinatario = tipoDestinatario;
    }

    public Long getIdEquipo() {
        return idEquipo;
    }

    public void setIdEquipo(Long idEquipo) {
        this.idEquipo = idEquipo;
    }

    public LocalDateTime getProgramarEnvio() {
        return programarEnvio;
    }

    public void setProgramarEnvio(LocalDateTime programarEnvio) {
        this.programarEnvio = programarEnvio;
    }

    public Integer getTotalDestinatarios() {
        return totalDestinatarios;
    }

    public void setTotalDestinatarios(Integer totalDestinatarios) {
        this.totalDestinatarios = totalDestinatarios;
    }

    public Integer getExitosos() {
        return exitosos;
    }

    public void setExitosos(Integer exitosos) {
        this.exitosos = exitosos;
    }

    public Integer getFallidos() {
        return fallidos;
    }

    public void setFallidos(Integer fallidos) {
        this.fallidos = fallidos;
    }
}
