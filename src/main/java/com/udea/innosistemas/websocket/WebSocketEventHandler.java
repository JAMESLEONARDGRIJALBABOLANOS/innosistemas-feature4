package com.udea.innosistemas.websocket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;
import org.springframework.web.socket.messaging.SessionUnsubscribeEvent;

/**
 * Handler de eventos de conexión WebSocket
 * Monitorea y registra eventos del ciclo de vida de las conexiones WebSocket
 *
 * Eventos manejados:
 * - Conexión establecida
 * - Desconexión
 * - Suscripción a tópicos
 * - Desuscripción de tópicos
 *
 * Autor: Fábrica-Escuela de Software UdeA
 * Versión: 1.0.0
 */
@Component
public class WebSocketEventHandler {

    private static final Logger logger = LoggerFactory.getLogger(WebSocketEventHandler.class);

    /**
     * Se ejecuta cuando un cliente establece una conexión WebSocket exitosamente
     */
    @EventListener
    public void handleWebSocketConnectListener(SessionConnectedEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = headerAccessor.getSessionId();

        Authentication auth = (Authentication) headerAccessor.getUser();
        String username = auth != null ? auth.getName() : "anonymous";

        logger.info("WebSocket conectado - SessionId: {}, Usuario: {}", sessionId, username);
    }

    /**
     * Se ejecuta cuando un cliente se desconecta del WebSocket
     */
    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = headerAccessor.getSessionId();

        Authentication auth = (Authentication) headerAccessor.getUser();
        String username = auth != null ? auth.getName() : "anonymous";

        logger.info("WebSocket desconectado - SessionId: {}, Usuario: {}", sessionId, username);
    }

    /**
     * Se ejecuta cuando un cliente se suscribe a un tópico
     */
    @EventListener
    public void handleSessionSubscribeEvent(SessionSubscribeEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = headerAccessor.getSessionId();
        String destination = headerAccessor.getDestination();

        Authentication auth = (Authentication) headerAccessor.getUser();
        String username = auth != null ? auth.getName() : "anonymous";

        logger.info("Usuario {} suscrito a {} - SessionId: {}", username, destination, sessionId);
    }

    /**
     * Se ejecuta cuando un cliente se desuscribe de un tópico
     */
    @EventListener
    public void handleSessionUnsubscribeEvent(SessionUnsubscribeEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = headerAccessor.getSessionId();
        String subscriptionId = headerAccessor.getSubscriptionId();

        Authentication auth = (Authentication) headerAccessor.getUser();
        String username = auth != null ? auth.getName() : "anonymous";

        logger.info("Usuario {} desuscrito de subscripción {} - SessionId: {}",
                username, subscriptionId, sessionId);
    }
}
