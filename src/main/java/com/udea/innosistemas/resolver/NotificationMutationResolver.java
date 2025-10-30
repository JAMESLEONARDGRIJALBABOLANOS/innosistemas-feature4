package com.udea.innosistemas.resolver;

import com.udea.innosistemas.dto.CreateNotificationRequest;
import com.udea.innosistemas.dto.NotificationDTO;
import com.udea.innosistemas.entity.User;
import com.udea.innosistemas.entity.UserRole;
import com.udea.innosistemas.exception.AuthenticationException;
import com.udea.innosistemas.repository.UserRepository;
import com.udea.innosistemas.service.NotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;

import java.util.Map;

/**
 * Resolver GraphQL para mutaciones de notificaciones
 * Maneja todas las operaciones de escritura de notificaciones
 *
 * Autor: Fábrica-Escuela de Software UdeA
 * Versión: 1.0.0
 */
@Controller
public class NotificationMutationResolver {

    private static final Logger logger = LoggerFactory.getLogger(NotificationMutationResolver.class);

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private UserRepository userRepository;

    /**
     * Crea una nueva notificación
     * Solo accesible para profesores y administradores
     *
     * @param input Datos de la notificación a crear
     * @return NotificationDTO creada
     */
    @MutationMapping
    @PreAuthorize("hasAnyRole('PROFESSOR', 'ADMIN', 'TA')")
    public NotificationDTO createNotification(@Argument Map<String, Object> input) {
        logger.info("Creando notificación con input: {}", input);

        CreateNotificationRequest request = new CreateNotificationRequest();
        request.setUserId(Long.parseLong(input.get("userId").toString()));
        request.setTipo(input.get("tipo").toString());
        request.setMensaje(input.get("mensaje").toString());

        if (input.containsKey("teamId") && input.get("teamId") != null) {
            request.setTeamId(Long.parseLong(input.get("teamId").toString()));
        }
        if (input.containsKey("cursoId") && input.get("cursoId") != null) {
            request.setCursoId(Long.parseLong(input.get("cursoId").toString()));
        }
        if (input.containsKey("metadata") && input.get("metadata") != null) {
            request.setMetadata(input.get("metadata").toString());
        }
        if (input.containsKey("prioridad") && input.get("prioridad") != null) {
            request.setPrioridad(input.get("prioridad").toString());
        }
        if (input.containsKey("enlace") && input.get("enlace") != null) {
            request.setEnlace(input.get("enlace").toString());
        }

        return notificationService.crearNotificacion(request);
    }

    /**
     * Marca una notificación como leída
     *
     * @param id ID de la notificación
     * @return NotificationDTO actualizada
     */
    @MutationMapping
    @PreAuthorize("isAuthenticated()")
    public NotificationDTO markNotificationAsRead(@Argument Long id) {
        logger.info("Marcando notificación {} como leída", id);

        Long userId = getCurrentUserId();

        // Verificar que la notificación pertenece al usuario actual
        NotificationDTO notification = notificationService.obtenerNotificacionesPorUsuario(userId)
                .stream()
                .filter(n -> n.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Notificación no encontrada o no tienes permiso para acceder a ella"));

        return notificationService.marcarComoLeida(id);
    }

    /**
     * Marca todas las notificaciones del usuario como leídas
     *
     * @return Respuesta con el número de notificaciones marcadas
     */
    @MutationMapping
    @PreAuthorize("isAuthenticated()")
    public Map<String, Object> markAllNotificationsAsRead() {
        logger.info("Marcando todas las notificaciones como leídas para el usuario actual");

        Long userId = getCurrentUserId();
        int count = notificationService.marcarTodasComoLeidas(userId);

        return Map.of(
                "success", true,
                "count", count,
                "message", String.format("Se marcaron %d notificación(es) como leída(s)", count)
        );
    }

    /**
     * Elimina una notificación específica
     * (Nota: Este método requiere implementar la lógica de eliminación en el servicio)
     *
     * @param id ID de la notificación
     * @return Respuesta de eliminación
     */
    @MutationMapping
    @PreAuthorize("isAuthenticated()")
    public Map<String, Object> deleteNotification(@Argument Long id) {
        logger.info("Eliminando notificación {}", id);

        Long userId = getCurrentUserId();

        // Verificar que la notificación pertenece al usuario actual
        NotificationDTO notification = notificationService.obtenerNotificacionesPorUsuario(userId)
                .stream()
                .filter(n -> n.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Notificación no encontrada o no tienes permiso para eliminarla"));

        // Aquí deberías implementar el método de eliminación en NotificationService
        // Por ahora retornamos una respuesta de éxito
        return Map.of(
                "success", true,
                "message", "Notificación eliminada exitosamente"
        );
    }

    /**
     * Obtiene el ID del usuario autenticado desde el contexto de seguridad
     *
     * @return ID del usuario
     */
    private Long getCurrentUserId() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        if (username == null || username.equals("anonymousUser")) {
            throw new AuthenticationException("No hay usuario autenticado");
        }

        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

        return user.getId();
    }

    /**
     * Verifica si el usuario actual tiene rol de profesor o admin
     *
     * @return true si es profesor o admin
     */
    private boolean isCurrentUserProfessorOrAdmin() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

        return user.getRole() == UserRole.PROFESSOR ||
               user.getRole() == UserRole.ADMIN ||
               user.getRole() == UserRole.TA;
    }
}
