package com.udea.innosistemas.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "destinatario_correo")
public class DestinatarioCorreo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idDestinatario;

    private Long idCorreo;
    private Long idUsuario;
    private String estado;
    @Column(columnDefinition = "TEXT")
    private String errorEnvio;
    private LocalDateTime fechaEnvio;
    private LocalDateTime fechaLectura;

    public Long getIdDestinatario() {
        return idDestinatario;
    }

    public void setIdDestinatario(Long idDestinatario) {
        this.idDestinatario = idDestinatario;
    }

    public Long getIdCorreo() {
        return idCorreo;
    }

    public void setIdCorreo(Long idCorreo) {
        this.idCorreo = idCorreo;
    }

    public Long getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Long idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getErrorEnvio() {
        return errorEnvio;
    }

    public void setErrorEnvio(String errorEnvio) {
        this.errorEnvio = errorEnvio;
    }

    public LocalDateTime getFechaEnvio() {
        return fechaEnvio;
    }

    public void setFechaEnvio(LocalDateTime fechaEnvio) {
        this.fechaEnvio = fechaEnvio;
    }

    public LocalDateTime getFechaLectura() {
        return fechaLectura;
    }

    public void setFechaLectura(LocalDateTime fechaLectura) {
        this.fechaLectura = fechaLectura;
    }
}
