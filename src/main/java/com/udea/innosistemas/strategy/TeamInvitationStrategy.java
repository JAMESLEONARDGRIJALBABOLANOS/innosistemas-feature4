package com.udea.innosistemas.strategy;

import com.udea.innosistemas.dto.CreateNotificationRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Estrategia para procesar notificaciones de invitación a equipos
 * Patrón: Strategy
 *
 * Autor: Fábrica-Escuela de Software UdeA
 * Versión: 1.0.0
 */
@Component
public class TeamInvitationStrategy implements NotificationStrategy {

    private static final Logger logger = LoggerFactory.getLogger(TeamInvitationStrategy.class);
    private static final String NOTIFICATION_TYPE = "INVITACION_EQUIPO";

    @Override
    public CreateNotificationRequest process(CreateNotificationRequest request) {
        logger.debug("Procesando invitación de equipo para usuario {}", request.getUserId());

        // Enriquecer la notificación con datos específicos de invitación
        if (request.getPrioridad() == null) {
            request.setPrioridad("ALTA");
        }

        // Añadir enlace de acción si no existe
        if (request.getEnlace() == null && request.getTeamId() != null) {
            request.setEnlace("/teams/" + request.getTeamId() + "/join");
        }

        return request;
    }

    @Override
    public boolean validate(CreateNotificationRequest request) {
        if (request.getUserId() == null) {
            logger.error("Invitación de equipo sin userId");
            return false;
        }

        if (request.getTeamId() == null) {
            logger.error("Invitación de equipo sin teamId");
            return false;
        }

        if (request.getMensaje() == null || request.getMensaje().isBlank()) {
            logger.error("Invitación de equipo sin mensaje");
            return false;
        }

        return true;
    }

    @Override
    public String getNotificationType() {
        return NOTIFICATION_TYPE;
    }
}
