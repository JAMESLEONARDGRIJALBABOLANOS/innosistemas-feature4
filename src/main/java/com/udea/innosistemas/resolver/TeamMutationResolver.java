package com.udea.innosistemas.resolver;

import com.udea.innosistemas.dto.TeamDTO;
import com.udea.innosistemas.entity.Team;
import com.udea.innosistemas.entity.User;
import com.udea.innosistemas.exception.AuthenticationException;
import com.udea.innosistemas.repository.UserRepository;
import com.udea.innosistemas.service.TeamService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

/**
 * Resolver GraphQL para mutaciones de equipos
 * Maneja todas las operaciones de escritura de equipos
 *
 * Autor: Fábrica-Escuela de Software UdeA
 * Versión: 1.0.0
 */
@Controller
public class TeamMutationResolver {

    private static final Logger logger = LoggerFactory.getLogger(TeamMutationResolver.class);
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    @Autowired
    private TeamService teamService;

    @Autowired
    private UserRepository userRepository;

    /**
     * Crea un nuevo equipo
     * Solo accesible para profesores y admins
     *
     * @param input Datos del equipo a crear
     * @return TeamDTO creado
     */
    @MutationMapping
    @PreAuthorize("hasAnyRole('PROFESSOR', 'ADMIN')")
    public TeamDTO createTeam(@Argument Map<String, Object> input) {
        logger.info("Creando nuevo equipo: {}", input);

        Team team = new Team();
        team.setNombre(input.get("nombre").toString());

        if (input.containsKey("descripcion") && input.get("descripcion") != null) {
            team.setDescripcion(input.get("descripcion").toString());
        }

        if (input.containsKey("fechaLimite") && input.get("fechaLimite") != null) {
            String fechaStr = input.get("fechaLimite").toString();
            team.setFechaLimite(LocalDateTime.parse(fechaStr, DATE_FORMATTER));
        }

        if (input.containsKey("courseId") && input.get("courseId") != null) {
            team.setCourseId(Long.parseLong(input.get("courseId").toString()));
        }

        if (input.containsKey("maxMiembros") && input.get("maxMiembros") != null) {
            team.setMaxMiembros((Integer) input.get("maxMiembros"));
        }

        Team nuevoTeam = teamService.crearTeam(team);
        List<User> miembros = userRepository.findByTeamId(nuevoTeam.getId());

        return new TeamDTO(nuevoTeam, miembros);
    }

    /**
     * Actualiza un equipo existente
     * Solo accesible para profesores y admins
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

        if (input.containsKey("nombre") && input.get("nombre") != null) {
            teamActualizado.setNombre(input.get("nombre").toString());
        }

        if (input.containsKey("descripcion") && input.get("descripcion") != null) {
            teamActualizado.setDescripcion(input.get("descripcion").toString());
        }

        if (input.containsKey("fechaLimite") && input.get("fechaLimite") != null) {
            String fechaStr = input.get("fechaLimite").toString();
            teamActualizado.setFechaLimite(LocalDateTime.parse(fechaStr, DATE_FORMATTER));
        }

        if (input.containsKey("activo") && input.get("activo") != null) {
            teamActualizado.setActivo((Boolean) input.get("activo"));
        }

        if (input.containsKey("maxMiembros") && input.get("maxMiembros") != null) {
            teamActualizado.setMaxMiembros((Integer) input.get("maxMiembros"));
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
     *
     * @param teamId ID del equipo
     * @return TeamDTO al que se unió
     */
    @MutationMapping
    @PreAuthorize("isAuthenticated()")
    public TeamDTO joinTeam(@Argument Long teamId) {
        logger.info("Usuario actual uniéndose al equipo {}", teamId);

        User currentUser = getCurrentUser();
        Team team = teamService.unirseAEquipo(teamId, currentUser.getId());
        List<User> miembros = userRepository.findByTeamId(team.getId());

        return new TeamDTO(team, miembros);
    }

    /**
     * Un usuario abandona su equipo
     *
     * @param teamId ID del equipo
     * @return LeaveTeamResponse
     */
    @MutationMapping
    @PreAuthorize("isAuthenticated()")
    public Map<String, Object> leaveTeam(@Argument Long teamId) {
        logger.info("Usuario actual abandonando el equipo {}", teamId);

        User currentUser = getCurrentUser();

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
