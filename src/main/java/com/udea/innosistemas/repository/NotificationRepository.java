package com.udea.innosistemas.repository;

import com.udea.innosistemas.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repositorio para la entidad Notification
 * Proporciona operaciones CRUD y consultas personalizadas para notificaciones
 *
 * Autor: Fábrica-Escuela de Software UdeA
 * Versión: 1.0.0
 */
@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    /**
     * Busca todas las notificaciones de un usuario
     */
    List<Notification> findByUserId(Long userId);

    /**
     * Busca notificaciones no leídas de un usuario
     */
    List<Notification> findByUserIdAndLeida(Long userId, boolean leida);

    /**
     * Busca notificaciones de un equipo
     */
    List<Notification> findByTeamId(Long teamId);

    /**
     * Busca notificaciones de un curso
     */
    List<Notification> findByCursoId(Long cursoId);

    /**
     * Busca notificaciones por tipo
     */
    List<Notification> findByTipo(String tipo);

    /**
     * Busca notificaciones no leídas de un usuario ordenadas por fecha de creación
     */
    List<Notification> findByUserIdAndLeidaOrderByFechaCreacionDesc(Long userId, boolean leida);

    /**
     * Cuenta las notificaciones no leídas de un usuario
     */
    long countByUserIdAndLeida(Long userId, boolean leida);

    /**
     * Busca notificaciones de un usuario por prioridad
     */
    @Query("SELECT n FROM Notification n WHERE n.userId = :userId AND n.prioridad = :prioridad")
    List<Notification> findByUserIdAndPrioridad(Long userId, Notification.NotificationPriority prioridad);

    /**
     * Busca notificaciones que expiran en un rango de tiempo
     */
    @Query("SELECT n FROM Notification n WHERE n.expiraEn IS NOT NULL AND n.expiraEn BETWEEN :inicio AND :fin")
    List<Notification> findNotificacionesProximasAExpirar(LocalDateTime inicio, LocalDateTime fin);

    /**
     * Busca notificaciones expiradas
     */
    @Query("SELECT n FROM Notification n WHERE n.expiraEn IS NOT NULL AND n.expiraEn < :fecha")
    List<Notification> findNotificacionesExpiradas(LocalDateTime fecha);

    /**
     * Busca notificaciones recientes de un usuario (últimas N horas)
     */
    @Query("SELECT n FROM Notification n WHERE n.userId = :userId AND n.fechaCreacion > :fecha ORDER BY n.fechaCreacion DESC")
    List<Notification> findNotificacionesRecientes(Long userId, LocalDateTime fecha);

    /**
     * Elimina notificaciones antiguas leídas
     */
    @Query("DELETE FROM Notification n WHERE n.leida = true AND n.fechaLectura < :fecha")
    void deleteNotificacionesAntiguasLeidas(LocalDateTime fecha);
}
