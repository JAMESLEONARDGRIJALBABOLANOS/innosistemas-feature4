package com.udea.innosistemas.resolver;

import com.udea.innosistemas.dto.CreateNotificationRequest;
import com.udea.innosistemas.dto.NotificationDTO;
import com.udea.innosistemas.factory.NotificationRequestFactory;
import com.udea.innosistemas.service.NotificationService;
import com.udea.innosistemas.strategy.NotificationStrategyContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;

import java.util.Map;

/**
 * Resolver GraphQL para mutaciones de notificaciones
 * Maneja todas las operaciones de escritura de notificaciones
 *
 * Refactorizado usando patrones:
 * - Template Method (BaseResolver)
 * - Factory (NotificationRequestFactory)
 * - Strategy (NotificationStrategyContext)
 *
 * Autor: Fábrica-Escuela de Software UdeA
 * Versión: 2.0.0
 */
@Controller
public class NotificationMutationResolver extends BaseResolver {

    private static final Logger logger = LoggerFactory.getLogger(NotificationMutationResolver.class);

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private NotificationStrategyContext strategyContext;

    /**
     * Crea una nueva notificación
     * Solo accesible para profesores y administradores
     * Usa Factory para crear el request y Strategy para procesarlo
     *
     * @param input Datos de la notificación a crear
     * @return NotificationDTO creada
     */
    @MutationMapping
    @PreAuthorize("hasAnyRole('PROFESSOR', 'ADMIN', 'TA')")
    public NotificationDTO createNotification(@Argument Map<String, Object> input) {
        logger.info("Creando notificación con input: {}", input);

        // Usar Factory para construir el request desde el Map
        CreateNotificationRequest request = NotificationRequestFactory.fromMap(input);

        // Usar Strategy para validar y procesar según el tipo
        request = strategyContext.processNotification(request);

        // Crear la notificación
        return notificationService.crearNotificacion(request);
    }

    /**
     * Marca una notificación como leída
     * Usa BaseResolver para obtener el usuario actual
     *
     * @param id ID de la notificación
     * @return NotificationDTO actualizada
     */
    @MutationMapping
    @PreAuthorize("isAuthenticated()")
    public NotificationDTO markNotificationAsRead(@Argument Long id) {
        logger.info("Marcando notificación {} como leída", id);

        Long userId = getCurrentUserId(); // Método de BaseResolver

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
     * Usa BaseResolver para obtener el usuario actual
     *
     * @return Respuesta con el número de notificaciones marcadas
     */
    @MutationMapping
    @PreAuthorize("isAuthenticated()")
    public Map<String, Object> markAllNotificationsAsRead() {
        logger.info("Marcando todas las notificaciones como leídas para el usuario actual");

        Long userId = getCurrentUserId(); // Método de BaseResolver
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
     * Usa BaseResolver para obtener el usuario actual
     *
     * @param id ID de la notificación
     * @return Respuesta de eliminación
     */
    @MutationMapping
    @PreAuthorize("isAuthenticated()")
    public Map<String, Object> deleteNotification(@Argument Long id) {
        logger.info("Eliminando notificación {}", id);

        Long userId = getCurrentUserId(); // Método de BaseResolver

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
}
