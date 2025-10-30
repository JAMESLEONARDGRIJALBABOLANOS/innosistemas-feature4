package com.udea.innosistemas.strategy;

import com.udea.innosistemas.dto.CreateNotificationRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Estrategia genérica para notificaciones sin tipo específico
 * Patrón: Strategy (Default Strategy)
 *
 * Se usa cuando no hay una estrategia específica para el tipo de notificación
 *
 * Autor: Fábrica-Escuela de Software UdeA
 * Versión: 1.0.0
 */
@Component
public class GenericNotificationStrategy implements NotificationStrategy {

    private static final Logger logger = LoggerFactory.getLogger(GenericNotificationStrategy.class);
    private static final String NOTIFICATION_TYPE = "GENERIC";

    @Override
    public CreateNotificationRequest process(CreateNotificationRequest request) {
        logger.debug("Procesando notificación genérica para usuario {}", request.getUserId());

        // Establecer prioridad por defecto si no existe
        if (request.getPrioridad() == null) {
            request.setPrioridad("NORMAL");
        }

        return request;
    }

    @Override
    public boolean validate(CreateNotificationRequest request) {
        if (request.getUserId() == null) {
            logger.error("Notificación genérica sin userId");
            return false;
        }

        if (request.getMensaje() == null || request.getMensaje().isBlank()) {
            logger.error("Notificación genérica sin mensaje");
            return false;
        }

        if (request.getTipo() == null || request.getTipo().isBlank()) {
            logger.error("Notificación genérica sin tipo");
            return false;
        }

        return true;
    }

    @Override
    public String getNotificationType() {
        return NOTIFICATION_TYPE;
    }
}
