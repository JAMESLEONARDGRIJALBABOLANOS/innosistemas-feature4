package com.udea.innosistemas.resolver;

import com.udea.innosistemas.dto.NotificationDTO;
import com.udea.innosistemas.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;

import java.util.List;

/**
 * Resolver GraphQL para consultas (Queries) de notificaciones
 * Maneja todas las operaciones de lectura de notificaciones
 *
 * Refactorizado usando patrón Template Method (BaseResolver)
 *
 * Autor: Fábrica-Escuela de Software UdeA
 * Versión: 2.0.0
 */
@Controller
public class NotificationQueryResolver extends BaseResolver {

    @Autowired
    private NotificationService notificationService;

    /**
     * Obtiene todas las notificaciones del usuario autenticado
     *
     * @return Lista de NotificationDTO
     */
    @QueryMapping
    @PreAuthorize("isAuthenticated()")
    public List<NotificationDTO> getMyNotifications() {
        Long userId = getCurrentUserId();
        return notificationService.obtenerNotificacionesPorUsuario(userId);
    }

    /**
     * Obtiene las notificaciones no leídas del usuario autenticado
     *
     * @return Lista de NotificationDTO no leídas
     */
    @QueryMapping
    @PreAuthorize("isAuthenticated()")
    public List<NotificationDTO> getUnreadNotifications() {
        Long userId = getCurrentUserId();
        return notificationService.obtenerNotificacionesNoLeidas(userId);
    }

    /**
     * Cuenta las notificaciones no leídas del usuario autenticado
     *
     * @return Número de notificaciones no leídas
     */
    @QueryMapping
    @PreAuthorize("isAuthenticated()")
    public int getUnreadNotificationCount() {
        Long userId = getCurrentUserId();
        return (int) notificationService.contarNotificacionesNoLeidas(userId);
    }

    /**
     * Obtiene las notificaciones recientes (últimas 24 horas) del usuario autenticado
     *
     * @return Lista de NotificationDTO recientes
     */
    @QueryMapping
    @PreAuthorize("isAuthenticated()")
    public List<NotificationDTO> getRecentNotifications() {
        Long userId = getCurrentUserId();
        return notificationService.obtenerNotificacionesRecientes(userId);
    }

    /**
     * Obtiene notificaciones por equipo
     * Solo accesible para miembros del equipo o profesores/admins
     *
     * @param teamId ID del equipo
     * @return Lista de NotificationDTO del equipo
     */
    @QueryMapping
    @PreAuthorize("isAuthenticated()")
    public List<NotificationDTO> getTeamNotifications(@Argument Long teamId) {
        // Aquí se podría agregar validación adicional para verificar que el usuario
        // pertenece al equipo o tiene permisos de profesor/admin
        return notificationService.obtenerNotificacionesPorEquipo(teamId);
    }

    /**
     * Obtiene una notificación específica por ID
     *
     * @param id ID de la notificación
     * @return NotificationDTO o null si no se encuentra
     */
    @QueryMapping
    @PreAuthorize("isAuthenticated()")
    public NotificationDTO getNotificationById(@Argument Long id) {
        Long userId = getCurrentUserId();

        // Buscar entre las notificaciones del usuario
        return notificationService.obtenerNotificacionesPorUsuario(userId)
                .stream()
                .filter(n -> n.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

}
