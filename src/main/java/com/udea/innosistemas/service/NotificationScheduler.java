package com.udea.innosistemas.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

/**
 * Servicio con tareas programadas para el sistema de notificaciones
 * Responsabilidades:
 * - Verificar fechas límite próximas (cada 6 horas)
 * - Verificar fechas límite vencidas (cada hora)
 * - Limpiar notificaciones antiguas (cada día)
 *
 * Autor: Fábrica-Escuela de Software UdeA
 * Versión: 1.0.0
 */
@Service
public class NotificationScheduler {

    private static final Logger logger = LoggerFactory.getLogger(NotificationScheduler.class);

    @Autowired
    private TeamEventService teamEventService;

    @Autowired
    private NotificationService notificationService;

    /**
     * Verifica equipos con fechas límite próximas cada 6 horas
     * Cron: 0 0 */6 * * * (cada 6 horas en punto)
     */
    @Scheduled(cron = "0 0 */6 * * *")
    public void verificarFechasLimiteProximas() {
        logger.info("Ejecutando tarea programada: verificar fechas límite próximas");
        try {
            teamEventService.verificarFechasLimiteProximas();
        } catch (Exception e) {
            logger.error("Error en verificación de fechas límite próximas", e);
        }
    }

    /**
     * Verifica equipos con fechas límite vencidas cada hora
     * Cron: 0 0 * * * * (cada hora en punto)
     */
    @Scheduled(cron = "0 0 * * * *")
    public void verificarFechasLimiteVencidas() {
        logger.info("Ejecutando tarea programada: verificar fechas límite vencidas");
        try {
            teamEventService.verificarFechasLimiteVencidas();
        } catch (Exception e) {
            logger.error("Error en verificación de fechas límite vencidas", e);
        }
    }

    /**
     * Limpia notificaciones antiguas ya leídas cada día a las 2 AM
     * Cron: 0 0 2 * * * (a las 2:00 AM todos los días)
     */
    @Scheduled(cron = "0 0 2 * * *")
    public void limpiarNotificacionesAntiguas() {
        logger.info("Ejecutando tarea programada: limpiar notificaciones antiguas");
        try {
            notificationService.limpiarNotificacionesAntiguas();
        } catch (Exception e) {
            logger.error("Error en limpieza de notificaciones antiguas", e);
        }
    }
}
