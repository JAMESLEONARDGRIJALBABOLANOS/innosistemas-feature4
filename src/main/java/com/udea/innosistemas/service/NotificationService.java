package com.udea.innosistemas.service;

import com.udea.innosistemas.dto.CreateNotificationRequest;
import com.udea.innosistemas.dto.NotificationDTO;
import com.udea.innosistemas.entity.Notification;
import com.udea.innosistemas.event.NotificationEvent;
import com.udea.innosistemas.repository.NotificationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Servicio para gestionar notificaciones en el sistema
 * Responsabilidades:
 * - Crear notificaciones
 * - Obtener notificaciones por usuario
 * - Marcar notificaciones como leídas
 * - Obtener notificaciones no leídas
 * - Publicar eventos cuando se crean notificaciones
 *
 * Autor: Fábrica-Escuela de Software UdeA
 * Versión: 1.0.0
 */
@Service
public class NotificationService {

    private static final Logger logger = LoggerFactory.getLogger(NotificationService.class);

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    /**
     * Crea una nueva notificación y publica un evento
     *
     * @param userId ID del usuario destinatario
     * @param tipo Tipo de notificación
     * @param mensaje Mensaje de la notificación
     * @param teamId ID del equipo relacionado (opcional)
     * @param metadata Metadatos adicionales en formato JSON (opcional)
     * @return NotificationDTO con los datos de la notificación creada
     */
    @Transactional
    public NotificationDTO crearNotificacion(Long userId, String tipo, String mensaje, Long teamId, String metadata) {
        logger.info("Creando notificación para usuario {} de tipo {}", userId, tipo);

        Notification notification = new Notification(userId, mensaje, tipo);
        notification.setTeamId(teamId);
        notification.setMetadata(metadata);

        // Guardar la notificación
        notification = notificationRepository.save(notification);
        logger.debug("Notificación creada con ID: {}", notification.getId());

        // Publicar evento para que los listeners (WebSocket, GraphQL) lo procesen
        eventPublisher.publishEvent(new NotificationEvent(this, notification));

        return new NotificationDTO(notification);
    }

    /**
     * Crea una notificación desde un request DTO
     *
     * @param request Request con los datos de la notificación
     * @return NotificationDTO con los datos de la notificación creada
     */
    @Transactional
    public NotificationDTO crearNotificacion(CreateNotificationRequest request) {
        logger.info("Creando notificación desde request para usuario {}", request.getUserId());

        Notification notification = new Notification(
                request.getUserId(),
                request.getMensaje(),
                request.getTipo()
        );

        notification.setTeamId(request.getTeamId());
        notification.setCursoId(request.getCursoId());
        notification.setMetadata(request.getMetadata());
        notification.setEnlace(request.getEnlace());
        notification.setExpiraEn(request.getExpiraEn());

        if (request.getPrioridad() != null) {
            try {
                notification.setPrioridad(Notification.NotificationPriority.valueOf(request.getPrioridad()));
            } catch (IllegalArgumentException e) {
                logger.warn("Prioridad inválida: {}, usando NORMAL por defecto", request.getPrioridad());
                notification.setPrioridad(Notification.NotificationPriority.NORMAL);
            }
        }

        notification = notificationRepository.save(notification);
        logger.debug("Notificación creada con ID: {}", notification.getId());

        eventPublisher.publishEvent(new NotificationEvent(this, notification));

        return new NotificationDTO(notification);
    }

    /**
     * Obtiene todas las notificaciones de un usuario
     *
     * @param userId ID del usuario
     * @return Lista de NotificationDTO
     */
    public List<NotificationDTO> obtenerNotificacionesPorUsuario(Long userId) {
        logger.debug("Obteniendo notificaciones para usuario {}", userId);

        List<Notification> notifications = notificationRepository.findByUserId(userId);
        return notifications.stream()
                .map(NotificationDTO::new)
                .collect(Collectors.toList());
    }

    /**
     * Obtiene las notificaciones no leídas de un usuario
     *
     * @param userId ID del usuario
     * @return Lista de NotificationDTO no leídas
     */
    public List<NotificationDTO> obtenerNotificacionesNoLeidas(Long userId) {
        logger.debug("Obteniendo notificaciones no leídas para usuario {}", userId);

        List<Notification> notifications = notificationRepository
                .findByUserIdAndLeidaOrderByFechaCreacionDesc(userId, false);
        return notifications.stream()
                .map(NotificationDTO::new)
                .collect(Collectors.toList());
    }

    /**
     * Marca una notificación como leída
     *
     * @param notificationId ID de la notificación
     * @return NotificationDTO actualizada
     */
    @Transactional
    public NotificationDTO marcarComoLeida(Long notificationId) {
        logger.debug("Marcando notificación {} como leída", notificationId);

        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new IllegalArgumentException("Notificación no encontrada con ID: " + notificationId));

        notification.marcarComoLeida();
        notification = notificationRepository.save(notification);

        return new NotificationDTO(notification);
    }

    /**
     * Marca todas las notificaciones de un usuario como leídas
     *
     * @param userId ID del usuario
     * @return Número de notificaciones marcadas como leídas
     */
    @Transactional
    public int marcarTodasComoLeidas(Long userId) {
        logger.info("Marcando todas las notificaciones como leídas para usuario {}", userId);

        List<Notification> notificaciones = notificationRepository.findByUserIdAndLeida(userId, false);
        notificaciones.forEach(Notification::marcarComoLeida);
        notificationRepository.saveAll(notificaciones);

        return notificaciones.size();
    }

    /**
     * Cuenta las notificaciones no leídas de un usuario
     *
     * @param userId ID del usuario
     * @return Número de notificaciones no leídas
     */
    public long contarNotificacionesNoLeidas(Long userId) {
        return notificationRepository.countByUserIdAndLeida(userId, false);
    }

    /**
     * Obtiene las notificaciones recientes de un usuario (últimas 24 horas)
     *
     * @param userId ID del usuario
     * @return Lista de NotificationDTO recientes
     */
    public List<NotificationDTO> obtenerNotificacionesRecientes(Long userId) {
        LocalDateTime hace24Horas = LocalDateTime.now().minusHours(24);
        List<Notification> notifications = notificationRepository
                .findNotificacionesRecientes(userId, hace24Horas);
        return notifications.stream()
                .map(NotificationDTO::new)
                .collect(Collectors.toList());
    }

    /**
     * Elimina notificaciones antiguas ya leídas (más de 30 días)
     */
    @Transactional
    public void limpiarNotificacionesAntiguas() {
        logger.info("Limpiando notificaciones antiguas leídas");
        LocalDateTime hace30Dias = LocalDateTime.now().minusDays(30);
        notificationRepository.deleteNotificacionesAntiguasLeidas(hace30Dias);
    }

    /**
     * Obtiene notificaciones por equipo
     *
     * @param teamId ID del equipo
     * @return Lista de NotificationDTO del equipo
     */
    public List<NotificationDTO> obtenerNotificacionesPorEquipo(Long teamId) {
        logger.debug("Obteniendo notificaciones para equipo {}", teamId);
        List<Notification> notifications = notificationRepository.findByTeamId(teamId);
        return notifications.stream()
                .map(NotificationDTO::new)
                .collect(Collectors.toList());
    }
}
