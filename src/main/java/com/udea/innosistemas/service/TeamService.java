package com.udea.innosistemas.service;

import com.udea.innosistemas.entity.Team;
import com.udea.innosistemas.entity.User;
import com.udea.innosistemas.repository.TeamRepository;
import com.udea.innosistemas.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Servicio para gestionar equipos (Teams)
 * Proporciona operaciones CRUD y lógica de negocio para equipos
 *
 * Autor: Fábrica-Escuela de Software UdeA
 * Versión: 1.0.0
 */
@Service
public class TeamService {

    private static final Logger logger = LoggerFactory.getLogger(TeamService.class);

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TeamEventService teamEventService;

    /**
     * Crea un nuevo equipo
     *
     * @param team El equipo a crear
     * @return Team creado
     */
    @Transactional
    public Team crearTeam(Team team) {
        logger.info("Creando nuevo equipo: {}", team.getNombre());

        Team nuevoTeam = teamRepository.save(team);

        // Procesar evento de creación
        teamEventService.procesarEventoCreacionEquipo(nuevoTeam);

        return nuevoTeam;
    }

    /**
     * Obtiene un equipo por ID
     *
     * @param id ID del equipo
     * @return Optional con el equipo
     */
    public Optional<Team> obtenerTeamPorId(Long id) {
        return teamRepository.findById(id);
    }

    /**
     * Obtiene todos los equipos de un curso
     *
     * @param courseId ID del curso
     * @return Lista de equipos
     */
    public List<Team> obtenerTeamsPorCurso(Long courseId) {
        return teamRepository.findByCourseId(courseId);
    }

    /**
     * Obtiene equipos activos por curso
     *
     * @param courseId ID del curso
     * @return Lista de equipos activos
     */
    public List<Team> obtenerTeamsActivosPorCurso(Long courseId) {
        return teamRepository.findByCourseIdAndActivo(courseId, true);
    }

    /**
     * Actualiza un equipo existente
     *
     * @param id ID del equipo
     * @param teamActualizado Datos actualizados del equipo
     * @return Team actualizado
     */
    @Transactional
    public Team actualizarTeam(Long id, Team teamActualizado) {
        logger.info("Actualizando equipo: {}", id);

        Team team = teamRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Equipo no encontrado: " + id));

        // Actualizar campos
        if (teamActualizado.getNombre() != null) {
            team.setNombre(teamActualizado.getNombre());
        }
        if (teamActualizado.getDescripcion() != null) {
            team.setDescripcion(teamActualizado.getDescripcion());
        }
        if (teamActualizado.getMaxMiembros() != null) {
            team.setMaxMiembros(teamActualizado.getMaxMiembros());
        }

        Team updated = teamRepository.save(team);
        return updated;
    }

    /**
     * Actualiza la fecha límite de un equipo
     *
     * @param teamId ID del equipo
     * @param nuevaFechaLimite Nueva fecha límite
     * @return Team actualizado
     */
    @Transactional
    public Team actualizarFechaLimite(Long teamId, LocalDateTime nuevaFechaLimite) {
        logger.info("Actualizando fecha límite del equipo {} a {}", teamId, nuevaFechaLimite);

        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new IllegalArgumentException("Equipo no encontrado: " + teamId));

        team.setFechaLimite(nuevaFechaLimite);
        Team updated = teamRepository.save(team);

        // Procesar evento de actualización de fecha límite
        teamEventService.procesarEventoFechaLimite(updated, nuevaFechaLimite);

        return updated;
    }

    /**
     * Invita un usuario a unirse a un equipo
     *
     * @param teamId ID del equipo
     * @param userId ID del usuario a invitar
     * @return Long ID de la notificación de invitación (si se creó)
     */
    @Transactional
    public Long invitarUsuario(Long teamId, Long userId) {
        logger.info("Invitando usuario {} al equipo {}", userId, teamId);

        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new IllegalArgumentException("Equipo no encontrado: " + teamId));

        User usuario = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado: " + userId));

        // Verificar que el equipo puede aceptar más miembros
        if (!team.puedeAgregarMiembros()) {
            throw new IllegalStateException("El equipo ha alcanzado su límite de miembros");
        }

        // Procesar evento de invitación
        teamEventService.procesarEventoInvitacion(team, usuario);

        return null; // Retornar el ID de la notificación si es necesario
    }

    /**
     * Un usuario se une a un equipo
     *
     * @param teamId ID del equipo
     * @param userId ID del usuario
     * @return Team al que se unió
     */
    @Transactional
    public Team unirseAEquipo(Long teamId, Long userId) {
        logger.info("Usuario {} uniéndose al equipo {}", userId, teamId);

        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new IllegalArgumentException("Equipo no encontrado: " + teamId));

        User usuario = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado: " + userId));

        // Verificar que el equipo puede aceptar más miembros
        if (!team.puedeAgregarMiembros()) {
            throw new IllegalStateException("El equipo ha alcanzado su límite de miembros");
        }

        // Asignar el usuario al equipo
        usuario.setTeamId(teamId);
        userRepository.save(usuario);

        // Procesar evento de miembro unido
        teamEventService.procesarEventoMiembroUnido(team, usuario);

        return team;
    }

    /**
     * Un usuario abandona un equipo
     *
     * @param teamId ID del equipo
     * @param userId ID del usuario
     */
    @Transactional
    public void abandonarEquipo(Long teamId, Long userId) {
        logger.info("Usuario {} abandonando el equipo {}", userId, teamId);

        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new IllegalArgumentException("Equipo no encontrado: " + teamId));

        User usuario = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado: " + userId));

        // Verificar que el usuario pertenece al equipo
        if (!teamId.equals(usuario.getTeamId())) {
            throw new IllegalStateException("El usuario no pertenece a este equipo");
        }

        // Remover al usuario del equipo
        usuario.setTeamId(null);
        userRepository.save(usuario);

        // Procesar evento de miembro abandona
        teamEventService.procesarEventoMiembroAbandona(team, usuario);
    }

    /**
     * Elimina un equipo
     *
     * @param teamId ID del equipo a eliminar
     */
    @Transactional
    public void eliminarTeam(Long teamId) {
        logger.info("Eliminando equipo: {}", teamId);

        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new IllegalArgumentException("Equipo no encontrado: " + teamId));

        // Remover a todos los usuarios del equipo
        List<User> miembros = userRepository.findByTeamId(teamId);
        miembros.forEach(u -> u.setTeamId(null));
        userRepository.saveAll(miembros);

        // Marcar el equipo como inactivo en lugar de eliminarlo físicamente
        team.setActivo(false);
        teamRepository.save(team);

        logger.info("Equipo {} marcado como inactivo", teamId);
    }

    /**
     * Obtiene equipos próximos a vencer
     *
     * @param dias Número de días antes de la fecha límite
     * @return Lista de equipos próximos a vencer
     */
    public List<Team> obtenerTeamsProximosAVencer(int dias) {
        LocalDateTime ahora = LocalDateTime.now();
        LocalDateTime limite = ahora.plusDays(dias);
        return teamRepository.findTeamsProximosAVencer(ahora, limite);
    }
}
