package com.udea.innosistemas.strategy;

import com.udea.innosistemas.dto.CreateNotificationRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Contexto que gestiona y selecciona la estrategia adecuada para cada tipo de notificación
 * Patrón: Strategy Context
 *
 * Mantiene un registro de todas las estrategias disponibles y selecciona
 * la apropiada basándose en el tipo de notificación
 *
 * Autor: Fábrica-Escuela de Software UdeA
 * Versión: 1.0.0
 */
@Component
public class NotificationStrategyContext {

    private static final Logger logger = LoggerFactory.getLogger(NotificationStrategyContext.class);

    private final Map<String, NotificationStrategy> strategies = new HashMap<>();
    private final NotificationStrategy defaultStrategy;

    @Autowired
    public NotificationStrategyContext(List<NotificationStrategy> strategyList, GenericNotificationStrategy defaultStrategy) {
        this.defaultStrategy = defaultStrategy;

        // Registrar todas las estrategias disponibles
        for (NotificationStrategy strategy : strategyList) {
            String type = strategy.getNotificationType();
            strategies.put(type, strategy);
            logger.info("Registrada estrategia de notificación: {}", type);
        }
    }

    /**
     * Procesa una notificación usando la estrategia apropiada
     *
     * @param request Request de notificación a procesar
     * @return Request procesado
     * @throws IllegalArgumentException si la validación falla
     */
    public CreateNotificationRequest processNotification(CreateNotificationRequest request) {
        NotificationStrategy strategy = getStrategy(request.getTipo());

        logger.debug("Procesando notificación tipo '{}' con estrategia '{}'",
                request.getTipo(), strategy.getClass().getSimpleName());

        // Validar
        if (!strategy.validate(request)) {
            throw new IllegalArgumentException(
                    String.format("Notificación de tipo '%s' no válida", request.getTipo())
            );
        }

        // Procesar
        return strategy.process(request);
    }

    /**
     * Obtiene la estrategia apropiada para un tipo de notificación
     *
     * @param notificationType Tipo de notificación
     * @return Estrategia correspondiente o la estrategia por defecto
     */
    private NotificationStrategy getStrategy(String notificationType) {
        return strategies.getOrDefault(notificationType, defaultStrategy);
    }

    /**
     * Valida una notificación sin procesarla
     *
     * @param request Request a validar
     * @return true si es válida
     */
    public boolean validateNotification(CreateNotificationRequest request) {
        NotificationStrategy strategy = getStrategy(request.getTipo());
        return strategy.validate(request);
    }
}
