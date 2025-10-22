package com.udea.innosistemas.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "historial_comunicaciones")
public class HistorialComunicaciones {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idHistorial;

    private String tipo;
    private Long idReferencia;
    private String titulo;
    @Column(columnDefinition = "TEXT")
    private String descripcion;
    private LocalDateTime fecha;
    private Long idUsuarioRemitente;
    private Long idUsuarioDestinatario;
    private Long idEquipoDestinatario;
    private String canal;
    @Column(columnDefinition = "JSON")
    private String metadata;

    public Long getIdHistorial() {
        return idHistorial;
    }

    public void setIdHistorial(Long idHistorial) {
        this.idHistorial = idHistorial;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public Long getIdReferencia() {
        return idReferencia;
    }

    public void setIdReferencia(Long idReferencia) {
        this.idReferencia = idReferencia;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }

    public Long getIdUsuarioRemitente() {
        return idUsuarioRemitente;
    }

    public void setIdUsuarioRemitente(Long idUsuarioRemitente) {
        this.idUsuarioRemitente = idUsuarioRemitente;
    }

    public Long getIdUsuarioDestinatario() {
        return idUsuarioDestinatario;
    }

    public void setIdUsuarioDestinatario(Long idUsuarioDestinatario) {
        this.idUsuarioDestinatario = idUsuarioDestinatario;
    }

    public Long getIdEquipoDestinatario() {
        return idEquipoDestinatario;
    }

    public void setIdEquipoDestinatario(Long idEquipoDestinatario) {
        this.idEquipoDestinatario = idEquipoDestinatario;
    }

    public String getCanal() {
        return canal;
    }

    public void setCanal(String canal) {
        this.canal = canal;
    }

    public String getMetadata() {
        return metadata;
    }

    public void setMetadata(String metadata) {
        this.metadata = metadata;
    }
}
