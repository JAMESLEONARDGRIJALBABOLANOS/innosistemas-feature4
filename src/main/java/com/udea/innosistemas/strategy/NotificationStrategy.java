package com.udea.innosistemas.strategy;

import com.udea.innosistemas.dto.CreateNotificationRequest;

/**
 * Interface para estrategias de procesamiento de notificaciones
 * Patrón: Strategy
 *
 * Permite diferentes comportamientos de procesamiento según el tipo de notificación
 * sin necesidad de estructuras condicionales complejas
 *
 * Autor: Fábrica-Escuela de Software UdeA
 * Versión: 1.0.0
 */
public interface NotificationStrategy {

    /**
     * Procesa una solicitud de notificación según la estrategia específica
     *
     * @param request Request de notificación a procesar
     * @return Request procesado y enriquecido
     */
    CreateNotificationRequest process(CreateNotificationRequest request);

    /**
     * Valida si la notificación cumple con los requisitos de esta estrategia
     *
     * @param request Request a validar
     * @return true si es válida
     */
    boolean validate(CreateNotificationRequest request);

    /**
     * Retorna el tipo de notificación que maneja esta estrategia
     *
     * @return Tipo de notificación (ej: "INVITACION_EQUIPO")
     */
    String getNotificationType();
}
