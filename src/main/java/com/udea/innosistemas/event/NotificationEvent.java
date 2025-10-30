package com.udea.innosistemas.event;

import com.udea.innosistemas.entity.Notification;
import org.springframework.context.ApplicationEvent;

/**
 * Evento de dominio para notificaciones
 * Se publica cuando se crea una nueva notificación
 * Utilizado para desacoplar la creación de notificaciones de su distribución (WebSocket, GraphQL)
 *
 * Autor: Fábrica-Escuela de Software UdeA
 * Versión: 1.0.0
 */
public class NotificationEvent extends ApplicationEvent {

    private final Notification notification;
    private final boolean enviarPorWebSocket;
    private final boolean enviarPorGraphQL;

    public NotificationEvent(Object source, Notification notification) {
        super(source);
        this.notification = notification;
        this.enviarPorWebSocket = true;
        this.enviarPorGraphQL = true;
    }

    public NotificationEvent(Object source, Notification notification, boolean enviarPorWebSocket, boolean enviarPorGraphQL) {
        super(source);
        this.notification = notification;
        this.enviarPorWebSocket = enviarPorWebSocket;
        this.enviarPorGraphQL = enviarPorGraphQL;
    }

    public Notification getNotification() {
        return notification;
    }

    public boolean isEnviarPorWebSocket() {
        return enviarPorWebSocket;
    }

    public boolean isEnviarPorGraphQL() {
        return enviarPorGraphQL;
    }

    @Override
    public String toString() {
        return "NotificationEvent{" +
                "notificationId=" + notification.getId() +
                ", userId=" + notification.getUserId() +
                ", tipo='" + notification.getTipo() + '\'' +
                ", enviarPorWebSocket=" + enviarPorWebSocket +
                ", enviarPorGraphQL=" + enviarPorGraphQL +
                '}';
    }
}
