package com.udea.innosistemas.repository;

import com.udea.innosistemas.entity.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repositorio para la entidad Team
 * Proporciona operaciones CRUD y consultas personalizadas para equipos
 *
 * Autor: Fábrica-Escuela de Software UdeA
 * Versión: 1.0.0
 */
@Repository
public interface TeamRepository extends JpaRepository<Team, Long> {

    /**
     * Busca equipos por curso
     */
    List<Team> findByCourseId(Long courseId);

    /**
     * Busca equipos activos
     */
    List<Team> findByActivo(boolean activo);

    /**
     * Busca equipos activos por curso
     */
    List<Team> findByCourseIdAndActivo(Long courseId, boolean activo);

    /**
     * Busca equipos cuya fecha límite esté próxima a vencer
     */
    @Query("SELECT t FROM Team t WHERE t.fechaLimite IS NOT NULL AND t.fechaLimite BETWEEN :inicio AND :fin")
    List<Team> findTeamsProximosAVencer(LocalDateTime inicio, LocalDateTime fin);

    /**
     * Busca equipos vencidos
     */
    @Query("SELECT t FROM Team t WHERE t.fechaLimite IS NOT NULL AND t.fechaLimite < :fecha")
    List<Team> findTeamsVencidos(LocalDateTime fecha);

    /**
     * Busca equipos por nombre (búsqueda parcial)
     */
    List<Team> findByNombreContainingIgnoreCase(String nombre);
}
