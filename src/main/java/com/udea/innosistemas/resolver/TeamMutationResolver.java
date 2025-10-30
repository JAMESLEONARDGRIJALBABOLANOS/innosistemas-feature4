package com.udea.innosistemas.resolver;

import com.udea.innosistemas.dto.TeamDTO;
import com.udea.innosistemas.entity.Team;
import com.udea.innosistemas.entity.User;
import com.udea.innosistemas.service.TeamService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

/**
 * Resolver GraphQL para mutaciones de equipos
 * Maneja todas las operaciones de escritura de equipos
 *
 * Refactorizado usando patrón Template Method (BaseResolver)
 *
 * Autor: Fábrica-Escuela de Software UdeA
 * Versión: 2.0.0
 */
@Controller
public class TeamMutationResolver extends BaseResolver {

    private static final Logger logger = LoggerFactory.getLogger(TeamMutationResolver.class);
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    @Autowired
    private TeamService teamService;

    /**
     * Crea un nuevo equipo
     * Solo accesible para profesores y admins
     * Usa BaseResolver para extraer valores del Map
     *
     * @param input Datos del equipo a crear
     * @return TeamDTO creado
     */
    @MutationMapping
    @PreAuthorize("hasAnyRole('PROFESSOR', 'ADMIN')")
    public TeamDTO createTeam(@Argument Map<String, Object> input) {
        logger.info("Creando nuevo equipo: {}", input);

        Team team = new Team();
        team.setNombre(extractString(input, "nombre")); // Método de BaseResolver

        String descripcion = extractString(input, "descripcion");
        if (descripcion != null) {
            team.setDescripcion(descripcion);
        }

        String fechaLimite = extractString(input, "fechaLimite");
        if (fechaLimite != null) {
            team.setFechaLimite(LocalDateTime.parse(fechaLimite, DATE_FORMATTER));
        }

        Long courseId = extractLong(input, "courseId");
        if (courseId != null) {
            team.setCourseId(courseId);
        }

        Integer maxMiembros = extractInteger(input, "maxMiembros");
        if (maxMiembros != null) {
            team.setMaxMiembros(maxMiembros);
        }

        Team nuevoTeam = teamService.crearTeam(team);
        List<User> miembros = userRepository.findByTeamId(nuevoTeam.getId());

        return new TeamDTO(nuevoTeam, miembros);
    }

    /**
     * Actualiza un equipo existente
     * Solo accesible para profesores y admins
     * Usa BaseResolver para extraer valores del Map
     *
     * @param id ID del equipo a actualizar
     * @param input Datos actualizados
     * @return TeamDTO actualizado
     */
    @MutationMapping
    @PreAuthorize("hasAnyRole('PROFESSOR', 'ADMIN')")
    public TeamDTO updateTeam(@Argument Long id, @Argument Map<String, Object> input) {
        logger.info("Actualizando equipo {}: {}", id, input);

        Team teamActualizado = new Team();

        String nombre = extractString(input, "nombre");
        if (nombre != null) {
            teamActualizado.setNombre(nombre);
        }

        String descripcion = extractString(input, "descripcion");
        if (descripcion != null) {
            teamActualizado.setDescripcion(descripcion);
        }

        String fechaLimite = extractString(input, "fechaLimite");
        if (fechaLimite != null) {
            teamActualizado.setFechaLimite(LocalDateTime.parse(fechaLimite, DATE_FORMATTER));
        }

        Boolean activo = extractBoolean(input, "activo");
        if (activo != null) {
            teamActualizado.setActivo(activo);
        }

        Integer maxMiembros = extractInteger(input, "maxMiembros");
        if (maxMiembros != null) {
            teamActualizado.setMaxMiembros(maxMiembros);
        }

        Team team = teamService.actualizarTeam(id, teamActualizado);
        List<User> miembros = userRepository.findByTeamId(team.getId());

        return new TeamDTO(team, miembros);
    }

    /**
     * Actualiza la fecha límite de un equipo
     * Solo accesible para profesores y admins
     *
     * @param teamId ID del equipo
     * @param deadline Nueva fecha límite (formato ISO)
     * @return TeamDTO actualizado
     */
    @MutationMapping
    @PreAuthorize("hasAnyRole('PROFESSOR', 'ADMIN')")
    public TeamDTO updateTeamDeadline(@Argument Long teamId, @Argument String deadline) {
        logger.info("Actualizando fecha límite del equipo {} a {}", teamId, deadline);

        LocalDateTime nuevaFechaLimite = LocalDateTime.parse(deadline, DATE_FORMATTER);
        Team team = teamService.actualizarFechaLimite(teamId, nuevaFechaLimite);
        List<User> miembros = userRepository.findByTeamId(team.getId());

        return new TeamDTO(team, miembros);
    }

    /**
     * Invita a un usuario a unirse a un equipo
     *
     * @param teamId ID del equipo
     * @param userId ID del usuario a invitar
     * @return InvitationResponse
     */
    @MutationMapping
    @PreAuthorize("hasAnyRole('PROFESSOR', 'ADMIN', 'TA')")
    public Map<String, Object> inviteUserToTeam(@Argument Long teamId, @Argument Long userId) {
        logger.info("Invitando usuario {} al equipo {}", userId, teamId);

        try {
            Long notificationId = teamService.invitarUsuario(teamId, userId);

            return Map.of(
                    "success", true,
                    "message", "Invitación enviada exitosamente",
                    "notificationId", notificationId != null ? notificationId : 0
            );
        } catch (Exception e) {
            logger.error("Error invitando usuario: {}", e.getMessage());
            return Map.of(
                    "success", false,
                    "message", "Error: " + e.getMessage()
            );
        }
    }

    /**
     * Un usuario se une a un equipo (acepta invitación)
     * Usa BaseResolver para obtener el usuario actual
     *
     * @param teamId ID del equipo
     * @return TeamDTO al que se unió
     */
    @MutationMapping
    @PreAuthorize("isAuthenticated()")
    public TeamDTO joinTeam(@Argument Long teamId) {
        logger.info("Usuario actual uniéndose al equipo {}", teamId);

        User currentUser = getCurrentUser(); // Método de BaseResolver
        Team team = teamService.unirseAEquipo(teamId, currentUser.getId());
        List<User> miembros = userRepository.findByTeamId(team.getId());

        return new TeamDTO(team, miembros);
    }

    /**
     * Un usuario abandona su equipo
     * Usa BaseResolver para obtener el usuario actual
     *
     * @param teamId ID del equipo
     * @return LeaveTeamResponse
     */
    @MutationMapping
    @PreAuthorize("isAuthenticated()")
    public Map<String, Object> leaveTeam(@Argument Long teamId) {
        logger.info("Usuario actual abandonando el equipo {}", teamId);

        User currentUser = getCurrentUser(); // Método de BaseResolver

        try {
            teamService.abandonarEquipo(teamId, currentUser.getId());

            return Map.of(
                    "success", true,
                    "message", "Has abandonado el equipo exitosamente"
            );
        } catch (Exception e) {
            logger.error("Error abandonando equipo: {}", e.getMessage());
            return Map.of(
                    "success", false,
                    "message", "Error: " + e.getMessage()
            );
        }
    }

    /**
     * Elimina un equipo
     * Solo accesible para profesores y admins
     *
     * @param id ID del equipo a eliminar
     * @return DeleteTeamResponse
     */
    @MutationMapping
    @PreAuthorize("hasAnyRole('PROFESSOR', 'ADMIN')")
    public Map<String, Object> deleteTeam(@Argument Long id) {
        logger.info("Eliminando equipo {}", id);

        try {
            teamService.eliminarTeam(id);

            return Map.of(
                    "success", true,
                    "message", "Equipo eliminado exitosamente"
            );
        } catch (Exception e) {
            logger.error("Error eliminando equipo: {}", e.getMessage());
            return Map.of(
                    "success", false,
                    "message", "Error: " + e.getMessage()
            );
        }
    }

}
