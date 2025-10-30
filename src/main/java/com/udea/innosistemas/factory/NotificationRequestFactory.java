package com.udea.innosistemas.factory;

import com.udea.innosistemas.dto.CreateNotificationRequest;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

/**
 * Factory para crear instancias de CreateNotificationRequest desde diferentes fuentes
 * Patrón: Factory
 *
 * Centraliza la lógica de creación de requests de notificación,
 * evitando código duplicado en los resolvers
 *
 * Autor: Fábrica-Escuela de Software UdeA
 * Versión: 1.0.0
 */
public class NotificationRequestFactory {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    /**
     * Crea un CreateNotificationRequest desde un Map (típicamente de GraphQL input)
     *
     * @param input Map con los datos de entrada
     * @return CreateNotificationRequest construido
     */
    public static CreateNotificationRequest fromMap(Map<String, Object> input) {
        CreateNotificationRequest.Builder builder = CreateNotificationRequest.builder();

        // Campos obligatorios
        if (input.containsKey("userId")) {
            builder.userId(extractLong(input, "userId"));
        }
        if (input.containsKey("tipo")) {
            builder.tipo(input.get("tipo").toString());
        }
        if (input.containsKey("mensaje")) {
            builder.mensaje(input.get("mensaje").toString());
        }

        // Campos opcionales
        Long teamId = extractLong(input, "teamId");
        if (teamId != null) {
            builder.teamId(teamId);
        }

        Long cursoId = extractLong(input, "cursoId");
        if (cursoId != null) {
            builder.cursoId(cursoId);
        }

        String metadata = extractString(input, "metadata");
        if (metadata != null) {
            builder.metadata(metadata);
        }

        String prioridad = extractString(input, "prioridad");
        if (prioridad != null) {
            builder.prioridad(prioridad);
        }

        String enlace = extractString(input, "enlace");
        if (enlace != null) {
            builder.enlace(enlace);
        }

        String expiraEn = extractString(input, "expiraEn");
        if (expiraEn != null) {
            builder.expiraEn(LocalDateTime.parse(expiraEn, DATE_FORMATTER));
        }

        return builder.build();
    }

    /**
     * Crea una notificación de invitación a equipo
     *
     * @param userId ID del usuario destinatario
     * @param teamId ID del equipo
     * @param teamName Nombre del equipo
     * @return CreateNotificationRequest para invitación
     */
    public static CreateNotificationRequest createTeamInvitation(Long userId, Long teamId, String teamName) {
        return CreateNotificationRequest.builder()
                .userId(userId)
                .tipo("INVITACION_EQUIPO")
                .mensaje(String.format("Has sido invitado a unirte al equipo '%s'", teamName))
                .teamId(teamId)
                .prioridad("ALTA")
                .build();
    }

    /**
     * Crea una notificación de cambio de fecha límite
     *
     * @param userId ID del usuario destinatario
     * @param teamId ID del equipo
     * @param teamName Nombre del equipo
     * @param newDeadline Nueva fecha límite
     * @return CreateNotificationRequest para cambio de fecha
     */
    public static CreateNotificationRequest createDeadlineChange(Long userId, Long teamId, String teamName, LocalDateTime newDeadline) {
        return CreateNotificationRequest.builder()
                .userId(userId)
                .tipo("CAMBIO_FECHA_LIMITE")
                .mensaje(String.format("La fecha límite del equipo '%s' ha sido actualizada", teamName))
                .teamId(teamId)
                .prioridad("ALTA")
                .metadata(String.format("{\"newDeadline\":\"%s\"}", newDeadline.toString()))
                .build();
    }

    /**
     * Crea una notificación de nuevo miembro en equipo
     *
     * @param userId ID del usuario destinatario
     * @param teamId ID del equipo
     * @param teamName Nombre del equipo
     * @param memberName Nombre del nuevo miembro
     * @return CreateNotificationRequest para nuevo miembro
     */
    public static CreateNotificationRequest createNewMemberNotification(Long userId, Long teamId, String teamName, String memberName) {
        return CreateNotificationRequest.builder()
                .userId(userId)
                .tipo("NUEVO_MIEMBRO")
                .mensaje(String.format("%s se ha unido al equipo '%s'", memberName, teamName))
                .teamId(teamId)
                .prioridad("NORMAL")
                .build();
    }

    /**
     * Crea una notificación de miembro que abandona el equipo
     *
     * @param userId ID del usuario destinatario
     * @param teamId ID del equipo
     * @param teamName Nombre del equipo
     * @param memberName Nombre del miembro que abandona
     * @return CreateNotificationRequest para miembro que abandona
     */
    public static CreateNotificationRequest createMemberLeftNotification(Long userId, Long teamId, String teamName, String memberName) {
        return CreateNotificationRequest.builder()
                .userId(userId)
                .tipo("MIEMBRO_ABANDONO")
                .mensaje(String.format("%s ha abandonado el equipo '%s'", memberName, teamName))
                .teamId(teamId)
                .prioridad("NORMAL")
                .build();
    }

    /**
     * Crea una notificación de recordatorio de fecha límite
     *
     * @param userId ID del usuario destinatario
     * @param teamId ID del equipo
     * @param teamName Nombre del equipo
     * @param daysRemaining Días restantes
     * @return CreateNotificationRequest para recordatorio
     */
    public static CreateNotificationRequest createDeadlineReminder(Long userId, Long teamId, String teamName, int daysRemaining) {
        String mensaje = daysRemaining == 1
                ? String.format("¡Atención! La fecha límite del equipo '%s' vence mañana", teamName)
                : String.format("La fecha límite del equipo '%s' vence en %d días", teamName, daysRemaining);

        return CreateNotificationRequest.builder()
                .userId(userId)
                .tipo("RECORDATORIO_FECHA_LIMITE")
                .mensaje(mensaje)
                .teamId(teamId)
                .prioridad("ALTA")
                .build();
    }

    // Métodos auxiliares privados

    private static Long extractLong(Map<String, Object> input, String key) {
        if (input.containsKey(key) && input.get(key) != null) {
            Object value = input.get(key);
            if (value instanceof Number) {
                return ((Number) value).longValue();
            }
            return Long.parseLong(value.toString());
        }
        return null;
    }

    private static String extractString(Map<String, Object> input, String key) {
        if (input.containsKey(key) && input.get(key) != null) {
            return input.get(key).toString();
        }
        return null;
    }
}
