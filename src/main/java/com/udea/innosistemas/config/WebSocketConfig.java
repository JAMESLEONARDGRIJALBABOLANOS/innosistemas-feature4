package com.udea.innosistemas.config;

import com.udea.innosistemas.security.GraphQLWebSocketAuthInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/**
 * Configuración de WebSocket con STOMP para Spring Boot
 * Habilita soporte para mensajería bidireccional en tiempo real
 *
 * Esta configuración es necesaria para:
 * - GraphQL Subscriptions vía WebSocket
 * - Notificaciones en tiempo real
 * - Eventos de equipo en tiempo real
 *
 * Protocolo: STOMP sobre WebSocket
 * Endpoint: /graphql-ws (para GraphQL Subscriptions)
 *
 * Autor: Fábrica-Escuela de Software UdeA
 * Versión: 1.0.0
 */
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Autowired
    private GraphQLWebSocketAuthInterceptor authInterceptor;

    /**
     * Configura los endpoints de WebSocket
     * Define dónde los clientes pueden conectarse
     */
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // Endpoint principal para GraphQL Subscriptions
        registry.addEndpoint("/graphql-ws")
                .setAllowedOriginPatterns("*") // Configurar según necesidades de CORS
                .withSockJS(); // Fallback a SockJS para navegadores que no soportan WebSocket

        // Endpoint sin SockJS para clientes nativos
        registry.addEndpoint("/graphql-ws")
                .setAllowedOriginPatterns("*");
    }

    /**
     * Configura el message broker
     * Define cómo se enrutan los mensajes
     */
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // Habilitar un simple message broker en memoria
        // Los mensajes con destino /topic o /queue serán enrutados al broker
        registry.enableSimpleBroker("/topic", "/queue", "/user");

        // Prefijo para mensajes desde el cliente al servidor
        registry.setApplicationDestinationPrefixes("/app");

        // Prefijo para mensajes dirigidos a usuarios específicos
        registry.setUserDestinationPrefix("/user");
    }

    /**
     * Configura interceptores para los canales de entrada
     * Aquí agregamos nuestro interceptor de autenticación JWT
     */
    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(authInterceptor);
    }
}
