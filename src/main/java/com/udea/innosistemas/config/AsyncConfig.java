package com.udea.innosistemas.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Configuración para habilitar procesamiento asíncrono y tareas programadas
 * Permite que los eventos de notificaciones se procesen en hilos separados
 * y que las verificaciones de fechas límite se ejecuten periódicamente
 *
 * Autor: Fábrica-Escuela de Software UdeA
 * Versión: 1.0.0
 */
@Configuration
@EnableAsync
@EnableScheduling
public class AsyncConfig {
    // La configuración por defecto de Spring es suficiente para este caso
    // Si se necesita personalizar el executor de tareas, se puede hacer aquí
}
