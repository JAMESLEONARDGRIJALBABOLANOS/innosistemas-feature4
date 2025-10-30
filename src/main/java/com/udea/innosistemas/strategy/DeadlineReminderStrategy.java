package com.udea.innosistemas.strategy;

import com.udea.innosistemas.dto.CreateNotificationRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Estrategia para procesar notificaciones de recordatorio de fecha límite
 * Patrón: Strategy
 *
 * Autor: Fábrica-Escuela de Software UdeA
 * Versión: 1.0.0
 */
@Component
public class DeadlineReminderStrategy implements NotificationStrategy {

    private static final Logger logger = LoggerFactory.getLogger(DeadlineReminderStrategy.class);
    private static final String NOTIFICATION_TYPE = "RECORDATORIO_FECHA_LIMITE";

    @Override
    public CreateNotificationRequest process(CreateNotificationRequest request) {
        logger.debug("Procesando recordatorio de fecha límite para usuario {}", request.getUserId());

        // Establecer prioridad ALTA para recordatorios
        if (request.getPrioridad() == null) {
            request.setPrioridad("ALTA");
        }

        // Añadir enlace al equipo si no existe
        if (request.getEnlace() == null && request.getTeamId() != null) {
            request.setEnlace("/teams/" + request.getTeamId());
        }

        return request;
    }

    @Override
    public boolean validate(CreateNotificationRequest request) {
        if (request.getUserId() == null) {
            logger.error("Recordatorio de fecha límite sin userId");
            return false;
        }

        if (request.getTeamId() == null) {
            logger.error("Recordatorio de fecha límite sin teamId");
            return false;
        }

        return true;
    }

    @Override
    public String getNotificationType() {
        return NOTIFICATION_TYPE;
    }
}
