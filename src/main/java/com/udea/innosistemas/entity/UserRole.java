package com.udea.innosistemas.entity;

/**
 * Enum que define los roles disponibles en el sistema InnoSistemas.
 * Estos roles determinan los permisos y accesos de cada usuario.
 *
 * Autor: Fábrica-Escuela de Software UdeA
 * Versión: 2.0.0
 */
public enum UserRole {
    /**
     * Estudiante - Usuario estándar con acceso a sus equipos y proyectos
     */
    STUDENT,

    /**
     * Profesor - Puede gestionar cursos, equipos y enviar notificaciones a sus estudiantes
     */
    PROFESSOR,

    /**
     * Administrador - Acceso completo al sistema
     */
    ADMIN,

    /**
     * Asistente de Enseñanza - Ayuda al profesor con permisos limitados
     */
    TA,

    /**
     * Emisor: usuarios con permiso para crear y enviar notificaciones o correos masivos
     */
    EMISOR,
    /**
     * Receptor: usuarios que reciben notificaciones, alertas o correos
     */
    RECEPTOR,
    /**
     * Permisos técnicos de lectura y diagnóstico.
     */
    DEVELOPER
}