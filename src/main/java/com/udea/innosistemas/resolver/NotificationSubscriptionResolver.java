package com.udea.innosistemas.resolver;

import com.udea.innosistemas.dto.NotificationDTO;
import com.udea.innosistemas.dto.TeamEventDTO;
import com.udea.innosistemas.entity.User;
import com.udea.innosistemas.exception.AuthenticationException;
import com.udea.innosistemas.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.SubscriptionMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * Resolver GraphQL para suscripciones (Subscriptions) de notificaciones
 * Maneja las suscripciones en tiempo real usando GraphQL Subscriptions con WebSockets
 *
 * Las suscripciones retornan Flux de Reactor para streaming de datos en tiempo real
 *
 * Autor: Fábrica-Escuela de Software UdeA
 * Versión: 1.0.0
 */
@Controller
public class NotificationSubscriptionResolver {

    private static final Logger logger = LoggerFactory.getLogger(NotificationSubscriptionResolver.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired(required = false) // Será inyectado cuando se cree
    private NotificationPublisher notificationPublisher;

    /**
     * Suscripción a notificaciones en tiempo real del usuario autenticado
     * Se emite cuando se crea una nueva notificación para el usuario
     *
     * @return Flux de NotificationDTO
     */
    @SubscriptionMapping
    @PreAuthorize("isAuthenticated()")
    public Flux<NotificationDTO> onNotificationReceived() {
        User currentUser = getCurrentUser();
        logger.info("Usuario {} suscrito a notificaciones en tiempo real", currentUser.getEmail());

        // Si notificationPublisher está disponible, usarlo
        if (notificationPublisher != null) {
            return notificationPublisher.getNotificationFlux(currentUser.getId());
        }

        // Fallback: retornar un Flux vacío con keepalive
        logger.warn("NotificationPublisher no disponible, retornando Flux vacío");
        return Flux.interval(Duration.ofSeconds(30))
                .map(i -> {
                    NotificationDTO keepalive = new NotificationDTO();
                    keepalive.setMensaje("keepalive");
                    return keepalive;
                });
    }

    /**
     * Suscripción a eventos de equipo en tiempo real
     * Se emite cuando ocurre un evento en el equipo del usuario
     *
     * @param teamId ID del equipo (opcional, si no se proporciona usa el equipo del usuario)
     * @return Flux de TeamEventPayload
     */
    @SubscriptionMapping
    @PreAuthorize("isAuthenticated()")
    public Flux<Map<String, Object>> onTeamEvent(@Argument(required = false) Long teamId) {
        User currentUser = getCurrentUser();
        Long targetTeamId = (teamId != null) ? teamId : currentUser.getTeamId();

        if (targetTeamId == null) {
            logger.warn("Usuario {} no tiene equipo asignado para suscripción", currentUser.getEmail());
            return Flux.empty();
        }

        logger.info("Usuario {} suscrito a eventos del equipo {}", currentUser.getEmail(), targetTeamId);

        // Si notificationPublisher está disponible, usarlo
        if (notificationPublisher != null) {
            return notificationPublisher.getTeamEventFlux(targetTeamId);
        }

        // Fallback: retornar un Flux vacío
        logger.warn("NotificationPublisher no disponible, retornando Flux vacío");
        return Flux.empty();
    }

    /**
     * Suscripción a contador de notificaciones no leídas
     * Se emite cuando cambia el número de notificaciones no leídas
     *
     * @return Flux de UnreadCountPayload
     */
    @SubscriptionMapping
    @PreAuthorize("isAuthenticated()")
    public Flux<Map<String, Object>> onUnreadCountChanged() {
        User currentUser = getCurrentUser();
        logger.info("Usuario {} suscrito a cambios de contador de no leídas", currentUser.getEmail());

        // Si notificationPublisher está disponible, usarlo
        if (notificationPublisher != null) {
            return notificationPublisher.getUnreadCountFlux(currentUser.getId());
        }

        // Fallback: emitir contador inicial
        logger.warn("NotificationPublisher no disponible, retornando Flux con contador inicial");
        return Flux.just(Map.of(
                "userId", currentUser.getId(),
                "count", 0,
                "timestamp", LocalDateTime.now().toString()
        ));
    }

    /**
     * Obtiene el usuario autenticado actual
     *
     * @return User
     */
    private User getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        if (username == null || username.equals("anonymousUser")) {
            throw new AuthenticationException("No hay usuario autenticado");
        }

        return userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));
    }
}
