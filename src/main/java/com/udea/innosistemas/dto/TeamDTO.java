package com.udea.innosistemas.dto;

import com.udea.innosistemas.entity.Team;
import com.udea.innosistemas.entity.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * DTO para transferir información de equipos
 *
 * Autor: Fábrica-Escuela de Software UdeA
 * Versión: 1.0.0
 */
public class TeamDTO {

    private Long id;
    private String nombre;
    private String descripcion;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaLimite;
    private Long courseId;
    private boolean activo;
    private Integer maxMiembros;
    private List<TeamMember> miembros;
    private boolean isVencido;
    private boolean puedeAgregarMiembros;

    public TeamDTO() {
    }

    public TeamDTO(Team team) {
        this.id = team.getId();
        this.nombre = team.getNombre();
        this.descripcion = team.getDescripcion();
        this.fechaCreacion = team.getFechaCreacion();
        this.fechaLimite = team.getFechaLimite();
        this.courseId = team.getCourseId();
        this.activo = team.isActivo();
        this.maxMiembros = team.getMaxMiembros();
        this.isVencido = team.isVencido();
        this.puedeAgregarMiembros = team.puedeAgregarMiembros();

        // Convertir miembros a DTOs si están disponibles
        if (team.getMiembros() != null) {
            this.miembros = team.getMiembros().stream()
                    .map(TeamMember::new)
                    .collect(Collectors.toList());
        }
    }

    public TeamDTO(Team team, List<User> miembros) {
        this(team);
        if (miembros != null) {
            this.miembros = miembros.stream()
                    .map(TeamMember::new)
                    .collect(Collectors.toList());
        }
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public LocalDateTime getFechaLimite() {
        return fechaLimite;
    }

    public void setFechaLimite(LocalDateTime fechaLimite) {
        this.fechaLimite = fechaLimite;
    }

    public Long getCourseId() {
        return courseId;
    }

    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    public Integer getMaxMiembros() {
        return maxMiembros;
    }

    public void setMaxMiembros(Integer maxMiembros) {
        this.maxMiembros = maxMiembros;
    }

    public List<TeamMember> getMiembros() {
        return miembros;
    }

    public void setMiembros(List<TeamMember> miembros) {
        this.miembros = miembros;
    }

    public boolean isVencido() {
        return isVencido;
    }

    public void setVencido(boolean vencido) {
        isVencido = vencido;
    }

    public boolean isPuedeAgregarMiembros() {
        return puedeAgregarMiembros;
    }

    public void setPuedeAgregarMiembros(boolean puedeAgregarMiembros) {
        this.puedeAgregarMiembros = puedeAgregarMiembros;
    }
}
