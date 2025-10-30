package com.udea.innosistemas.service;

import com.udea.innosistemas.entity.Notification;
import com.udea.innosistemas.entity.Team;
import com.udea.innosistemas.entity.User;
import com.udea.innosistemas.enums.TipoEvento;
import com.udea.innosistemas.enums.TipoNotificacion;
import com.udea.innosistemas.event.TeamEvent;
import com.udea.innosistemas.repository.TeamRepository;
import com.udea.innosistemas.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Servicio coordinador para distribuir notificaciones de eventos de equipos
 * Responsabilidades:
 * - Escuchar eventos de equipo (TeamEvent)
 * - Determinar destinatarios según el tipo de evento
 * - Crear notificaciones para cada miembro relevante
 * - Publicar eventos para WebSocket/GraphQL Subscription
 *
 * Este servicio implementa el patrón Observer para reaccionar a eventos de dominio
 * y desacoplar la lógica de negocio de la distribución de notificaciones
 *
 * Autor: Fábrica-Escuela de Software UdeA
 * Versión: 1.0.0
 */
@Service
public class TeamNotificationDispatcher {

    private static final Logger logger = LoggerFactory.getLogger(TeamNotificationDispatcher.class);

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private UserRepository userRepository;

    /**
     * Escucha eventos de equipo y los procesa de forma asíncrona
     * Determina los destinatarios y crea notificaciones apropiadas
     *
     * @param event Evento de equipo publicado
     */
    @Async
    @EventListener
    @Transactional
    public void onTeamEvent(TeamEvent event) {
        logger.info("Procesando evento de equipo: {}", event);

        try {
            // Obtener el equipo
            Team team = teamRepository.findById(event.getTeamId())
                    .orElseThrow(() -> new IllegalArgumentException("Equipo no encontrado: " + event.getTeamId()));

            // Determinar destinatarios según el tipo de evento
            List<User> destinatarios = determinarDestinatarios(event, team);

            if (destinatarios.isEmpty()) {
                logger.warn("No se encontraron destinatarios para el evento {}", event.getTipoEvento());
                return;
            }

            // Crear notificaciones para cada destinatario
            for (User destinatario : destinatarios) {
                crearNotificacionParaUsuario(event, team, destinatario);
            }

            logger.info("Notificaciones creadas para {} destinatario(s)", destinatarios.size());

        } catch (Exception e) {
            logger.error("Error procesando evento de equipo: {}", event, e);
        }
    }

    /**
     * Determina quiénes deben recibir notificaciones según el tipo de evento
     *
     * @param event Evento de equipo
     * @param team Equipo relacionado
     * @return Lista de usuarios destinatarios
     */
    private List<User> determinarDestinatarios(TeamEvent event, Team team) {
        TipoEvento tipoEvento = event.getTipoEvento();

        // Si es una invitación, solo notificar al usuario invitado
        if (tipoEvento == TipoEvento.INVITACION_EQUIPO && event.getUsuarioOrigenId() != null) {
            return userRepository.findById(event.getUsuarioOrigenId())
                    .map(List::of)
                    .orElse(List.of());
        }

        // Para otros eventos, notificar a todos los miembros del equipo
        if (tipoEvento.notificarATodos()) {
            List<User> miembros = userRepository.findByTeamId(team.getId());

            // Excluir al usuario que originó el evento (opcional)
            if (event.getUsuarioOrigenId() != null) {
                miembros.removeIf(u -> u.getId().equals(event.getUsuarioOrigenId()));
            }

            return miembros;
        }

        return List.of();
    }

    /**
     * Crea una notificación para un usuario específico basada en el evento
     *
     * @param event Evento de equipo
     * @param team Equipo relacionado
     * @param destinatario Usuario destinatario
     */
    private void crearNotificacionParaUsuario(TeamEvent event, Team team, User destinatario) {
        try {
            // Determinar el tipo de notificación
            String tipoNotificacion = determinarTipoNotificacion(event.getTipoEvento());

            // Construir el mensaje
            String mensaje = construirMensaje(event, team);

            // Determinar la prioridad
            Notification.NotificationPriority prioridad = determinarPrioridad(event.getTipoEvento());

            // Crear metadata con información del evento
            String metadata = construirMetadata(event, team);

            // Crear la notificación usando el servicio
            notificationService.crearNotificacion(
                    destinatario.getId(),
                    tipoNotificacion,
                    mensaje,
                    team.getId(),
                    metadata
            );

            logger.debug("Notificación creada para usuario {} del evento {}",
                    destinatario.getId(), event.getTipoEvento());

        } catch (Exception e) {
            logger.error("Error creando notificación para usuario {}: {}",
                    destinatario.getId(), e.getMessage());
        }
    }

    /**
     * Determina el tipo de notificación según el tipo de evento
     */
    private String determinarTipoNotificacion(TipoEvento tipoEvento) {
        return switch (tipoEvento) {
            case INVITACION_EQUIPO -> TipoNotificacion.INVITACION.name();
            case FECHA_LIMITE_PROXIMA, FECHA_LIMITE_ALCANZADA -> TipoNotificacion.FECHA_LIMITE.name();
            case FECHA_LIMITE_ACTUALIZADA -> TipoNotificacion.ALERTA.name();
            case EQUIPO_ELIMINADO -> TipoNotificacion.ALERTA.name();
            case TAREA_ASIGNADA, TAREA_COMPLETADA -> TipoNotificacion.TAREA.name();
            default -> TipoNotificacion.EQUIPO.name();
        };
    }

    /**
     * Construye el mensaje de la notificación
     */
    private String construirMensaje(TeamEvent event, Team team) {
        // Usar los detalles del evento o generar un mensaje predeterminado
        if (event.getDetalles() != null && !event.getDetalles().isEmpty()) {
            return event.getDetalles();
        }

        return String.format("%s - Equipo: %s",
                event.getTipoEvento().getDescripcionPredeterminada(),
                team.getNombre());
    }

    /**
     * Determina la prioridad de la notificación según el tipo de evento
     */
    private Notification.NotificationPriority determinarPrioridad(TipoEvento tipoEvento) {
        if (tipoEvento.esCritico()) {
            return Notification.NotificationPriority.ALTA;
        }

        return switch (tipoEvento) {
            case INVITACION_EQUIPO, TAREA_ASIGNADA -> Notification.NotificationPriority.ALTA;
            case FECHA_LIMITE_ACTUALIZADA, CAMBIO_LIDER -> Notification.NotificationPriority.NORMAL;
            default -> Notification.NotificationPriority.NORMAL;
        };
    }

    /**
     * Construye metadata en formato JSON para la notificación
     */
    private String construirMetadata(TeamEvent event, Team team) {
        StringBuilder metadata = new StringBuilder("{");
        metadata.append(String.format("\"tipoEvento\": \"%s\"", event.getTipoEvento()));
        metadata.append(String.format(", \"teamId\": %d", team.getId()));
        metadata.append(String.format(", \"teamNombre\": \"%s\"", team.getNombre()));

        if (event.getUsuarioOrigenId() != null) {
            metadata.append(String.format(", \"usuarioOrigenId\": %d", event.getUsuarioOrigenId()));
        }

        if (event.getMetadata() != null) {
            // Agregar metadata adicional del evento (sin las llaves externas)
            String eventMetadata = event.getMetadata().trim();
            if (eventMetadata.startsWith("{") && eventMetadata.endsWith("}")) {
                eventMetadata = eventMetadata.substring(1, eventMetadata.length() - 1);
            }
            metadata.append(", ").append(eventMetadata);
        }

        metadata.append("}");
        return metadata.toString();
    }

    /**
     * Método auxiliar para enviar notificaciones masivas a un equipo
     * Útil para eventos personalizados que no se originan desde TeamEvent
     *
     * @param teamId ID del equipo
     * @param mensaje Mensaje de la notificación
     * @param tipoNotificacion Tipo de notificación
     */
    public void enviarNotificacionAEquipo(Long teamId, String mensaje, String tipoNotificacion) {
        logger.info("Enviando notificación masiva al equipo {}", teamId);

        List<User> miembros = userRepository.findByTeamId(teamId);

        for (User miembro : miembros) {
            notificationService.crearNotificacion(
                    miembro.getId(),
                    tipoNotificacion,
                    mensaje,
                    teamId,
                    null
            );
        }

        logger.info("Notificación enviada a {} miembro(s) del equipo {}", miembros.size(), teamId);
    }
}
