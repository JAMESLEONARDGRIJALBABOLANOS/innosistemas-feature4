package com.udea.innosistemas.service;

import com.udea.innosistemas.dto.TeamEventDTO;
import com.udea.innosistemas.entity.Team;
import com.udea.innosistemas.entity.User;
import com.udea.innosistemas.enums.TipoEvento;
import com.udea.innosistemas.event.TeamEvent;
import com.udea.innosistemas.repository.TeamRepository;
import com.udea.innosistemas.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

/**
 * Servicio para gestionar eventos de equipos
 * Responsabilidades:
 * - Registrar eventos de equipos
 * - Procesar eventos de creación de equipos
 * - Procesar eventos de fecha límite
 * - Procesar eventos de invitación
 * - Publicar eventos de dominio
 *
 * Autor: Fábrica-Escuela de Software UdeA
 * Versión: 1.0.0
 */
@Service
public class TeamEventService {

    private static final Logger logger = LoggerFactory.getLogger(TeamEventService.class);
    private static final int DIAS_ALERTA_FECHA_LIMITE = 3; // Alertar 3 días antes

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private UserRepository userRepository;

    /**
     * Registra un evento de equipo y lo publica
     *
     * @param teamId ID del equipo
     * @param tipoEvento Tipo de evento
     * @param usuarioOrigenId ID del usuario que originó el evento
     * @param detalles Detalles adicionales del evento
     * @return TeamEventDTO con los datos del evento
     */
    public TeamEventDTO registrarEventoEquipo(Long teamId, TipoEvento tipoEvento, Long usuarioOrigenId, String detalles) {
        logger.info("Registrando evento {} para equipo {}", tipoEvento, teamId);

        TeamEventDTO eventDTO = new TeamEventDTO(teamId, tipoEvento, usuarioOrigenId, detalles);

        // Publicar evento de dominio
        TeamEvent event = new TeamEvent(this, teamId, tipoEvento, usuarioOrigenId, detalles);
        eventPublisher.publishEvent(event);

        return eventDTO;
    }

    /**
     * Procesa el evento de creación de un equipo
     * Genera notificaciones para todos los miembros iniciales
     *
     * @param team El equipo que fue creado
     */
    public void procesarEventoCreacionEquipo(Team team) {
        logger.info("Procesando evento de creación de equipo: {}", team.getId());

        String detalles = String.format("Se ha creado el equipo '%s'", team.getNombre());

        if (team.getFechaLimite() != null) {
            detalles += String.format(". Fecha límite: %s", team.getFechaLimite());
        }

        // Publicar evento
        TeamEvent event = new TeamEvent(
                this,
                team.getId(),
                TipoEvento.CREACION_EQUIPO,
                null, // Sin usuario origen específico
                detalles
        );
        eventPublisher.publishEvent(event);
    }

    /**
     * Procesa el evento de actualización de fecha límite
     * Genera notificaciones para todos los miembros del equipo
     *
     * @param team El equipo cuya fecha límite fue actualizada
     * @param fechaLimite La nueva fecha límite
     */
    public void procesarEventoFechaLimite(Team team, LocalDateTime fechaLimite) {
        logger.info("Procesando evento de actualización de fecha límite para equipo: {}", team.getId());

        String detalles = String.format(
                "La fecha límite del equipo '%s' ha sido actualizada a: %s",
                team.getNombre(),
                fechaLimite
        );

        // Calcular días restantes
        long diasRestantes = ChronoUnit.DAYS.between(LocalDateTime.now(), fechaLimite);

        // Publicar evento
        TeamEvent event = new TeamEvent(
                this,
                team.getId(),
                TipoEvento.FECHA_LIMITE_ACTUALIZADA,
                null,
                detalles,
                String.format("{\"diasRestantes\": %d}", diasRestantes)
        );
        eventPublisher.publishEvent(event);

        // Si la fecha límite está próxima, generar alerta adicional
        if (diasRestantes <= DIAS_ALERTA_FECHA_LIMITE && diasRestantes > 0) {
            procesarAlertaFechaLimiteProxima(team, diasRestantes);
        }
    }

    /**
     * Procesa el evento de invitación a un equipo
     * Genera notificación para el usuario invitado
     *
     * @param team El equipo al que se invita
     * @param usuarioInvitado El usuario que fue invitado
     */
    public void procesarEventoInvitacion(Team team, User usuarioInvitado) {
        logger.info("Procesando evento de invitación para usuario {} al equipo {}",
                usuarioInvitado.getId(), team.getId());

        String detalles = String.format(
                "Has sido invitado a unirte al equipo '%s'",
                team.getNombre()
        );

        if (team.getDescripcion() != null && !team.getDescripcion().isEmpty()) {
            detalles += String.format(". Descripción: %s", team.getDescripcion());
        }

        // Publicar evento (solo para el usuario invitado)
        TeamEvent event = new TeamEvent(
                this,
                team.getId(),
                TipoEvento.INVITACION_EQUIPO,
                usuarioInvitado.getId(), // Usuario invitado es el destinatario
                detalles,
                String.format("{\"usuarioInvitadoId\": %d}", usuarioInvitado.getId())
        );
        eventPublisher.publishEvent(event);
    }

    /**
     * Procesa cuando un miembro se une al equipo
     *
     * @param team El equipo
     * @param usuario El usuario que se unió
     */
    public void procesarEventoMiembroUnido(Team team, User usuario) {
        logger.info("Procesando evento de miembro unido: usuario {} al equipo {}",
                usuario.getId(), team.getId());

        String detalles = String.format(
                "%s se ha unido al equipo '%s'",
                usuario.getFullName(),
                team.getNombre()
        );

        TeamEvent event = new TeamEvent(
                this,
                team.getId(),
                TipoEvento.MIEMBRO_UNIDO,
                usuario.getId(),
                detalles
        );
        eventPublisher.publishEvent(event);
    }

    /**
     * Procesa cuando un miembro abandona el equipo
     *
     * @param team El equipo
     * @param usuario El usuario que abandonó
     */
    public void procesarEventoMiembroAbandona(Team team, User usuario) {
        logger.info("Procesando evento de miembro abandona: usuario {} del equipo {}",
                usuario.getId(), team.getId());

        String detalles = String.format(
                "%s ha abandonado el equipo '%s'",
                usuario.getFullName(),
                team.getNombre()
        );

        TeamEvent event = new TeamEvent(
                this,
                team.getId(),
                TipoEvento.MIEMBRO_ABANDONA,
                usuario.getId(),
                detalles
        );
        eventPublisher.publishEvent(event);
    }

    /**
     * Verifica y procesa alertas de fechas límite próximas
     * Este método puede ser llamado por un scheduler
     */
    public void verificarFechasLimiteProximas() {
        logger.info("Verificando fechas límite próximas");

        LocalDateTime ahora = LocalDateTime.now();
        LocalDateTime limite = ahora.plusDays(DIAS_ALERTA_FECHA_LIMITE);

        // Buscar equipos con fecha límite próxima
        teamRepository.findTeamsProximosAVencer(ahora, limite)
                .forEach(team -> {
                    long diasRestantes = ChronoUnit.DAYS.between(ahora, team.getFechaLimite());
                    procesarAlertaFechaLimiteProxima(team, diasRestantes);
                });
    }

    /**
     * Verifica y procesa equipos con fecha límite vencida
     * Este método puede ser llamado por un scheduler
     */
    public void verificarFechasLimiteVencidas() {
        logger.info("Verificando fechas límite vencidas");

        LocalDateTime ahora = LocalDateTime.now();

        // Buscar equipos vencidos
        teamRepository.findTeamsVencidos(ahora)
                .forEach(this::procesarAlertaFechaLimiteAlcanzada);
    }

    /**
     * Procesa alerta de fecha límite próxima
     */
    private void procesarAlertaFechaLimiteProxima(Team team, long diasRestantes) {
        String detalles = String.format(
                "¡Atención! La fecha límite del equipo '%s' está próxima. Quedan %d día(s)",
                team.getNombre(),
                diasRestantes
        );

        TeamEvent event = new TeamEvent(
                this,
                team.getId(),
                TipoEvento.FECHA_LIMITE_PROXIMA,
                null,
                detalles,
                String.format("{\"diasRestantes\": %d}", diasRestantes)
        );
        eventPublisher.publishEvent(event);
    }

    /**
     * Procesa alerta de fecha límite alcanzada
     */
    private void procesarAlertaFechaLimiteAlcanzada(Team team) {
        String detalles = String.format(
                "¡La fecha límite del equipo '%s' ha sido alcanzada!",
                team.getNombre()
        );

        TeamEvent event = new TeamEvent(
                this,
                team.getId(),
                TipoEvento.FECHA_LIMITE_ALCANZADA,
                null,
                detalles
        );
        eventPublisher.publishEvent(event);
    }
}
