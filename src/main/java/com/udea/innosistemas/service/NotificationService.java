package com.udea.innosistemas.service;

import com.udea.innosistemas.dto.ConfigInput;
import com.udea.innosistemas.entity.ConfiguracionNotificacion;
import com.udea.innosistemas.entity.Notificacion;
import com.udea.innosistemas.entity.HistorialComunicaciones;
import com.udea.innosistemas.repository.ConfiguracionNotificacionRepository;
import com.udea.innosistemas.repository.NotificacionRepository;
import com.udea.innosistemas.repository.HistorialComunicacionesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Servicio principal para la gestión de notificaciones y configuración de alertas.
 * Incluye métodos para enviar, listar y configurar preferencias del usuario.
 */
@Service
public class NotificationService {

    @Autowired
    private NotificacionRepository notificacionRepo;

    @Autowired
    private ConfiguracionNotificacionRepository configRepo;

    @Autowired
    private HistorialComunicacionesRepository historialRepo;

    @Autowired
    private PermissionService permService;

    @Autowired
    private UserService userService;

    /**
     * Obtiene o crea la configuración de notificaciones del usuario autenticado.
     */
    public ConfiguracionNotificacion getConfigByUserId(Long userId) {
        return configRepo.findByIdUsuario(userId).orElseGet(() -> {
            ConfiguracionNotificacion c = new ConfiguracionNotificacion();
            c.setIdUsuario(userId);
            c.setRecibirCorreos(true);
            c.setNotificacionesTareas(true);
            c.setNotificacionesEquipos(true);
            c.setNotificacionesSistema(true);
            c.setAlertasVencimiento(true);
            c.setFrecuenciaCorreos("inmediato");
            c.setCreatedAt(LocalDateTime.now());
            c.setUpdatedAt(LocalDateTime.now());
            return configRepo.save(c);
        });
    }

    /**
     * Guarda o actualiza la configuración de notificaciones del usuario.
     */
    public ConfiguracionNotificacion saveOrUpdateConfig(Long userId, ConfigInput input) {
        ConfiguracionNotificacion cfg = configRepo.findByIdUsuario(userId)
                .orElse(new ConfiguracionNotificacion());

        cfg.setIdUsuario(userId);
        cfg.setRecibirCorreos(input.getRecibirCorreos());
        cfg.setNotificacionesTareas(input.getNotificacionesTareas());
        cfg.setNotificacionesEquipos(input.getNotificacionesEquipos());
        cfg.setNotificacionesSistema(input.getNotificacionesSistema());
        cfg.setAlertasVencimiento(input.getAlertasVencimiento());
        cfg.setFrecuenciaCorreos(input.getFrecuenciaCorreos());
        cfg.setUpdatedAt(LocalDateTime.now());

        return configRepo.save(cfg);
    }

    /**
     * Envía una notificación a un usuario, respetando sus preferencias.
     */
    @PreAuthorize("hasAnyRole('EMISOR','ADMIN')")
    public Notificacion sendNotification(Notificacion n, Authentication auth) {
        // Verificar configuración del receptor
        if (n.getIdUsuario() != null) {
            configRepo.findByIdUsuario(n.getIdUsuario()).ifPresent(cfg -> {
                if (Boolean.FALSE.equals(cfg.getRecibirCorreos()) &&
                        "correo".equalsIgnoreCase(n.getCanal())) {
                    // Respetar preferencia: no enviar por correo
                    n.setCanal("in-app");
                }
            });
        }

        n.setFecha(LocalDateTime.now());
        n.setEstado("no_leida");

        Notificacion saved = notificacionRepo.save(n);

        // Guardar en historial
        HistorialComunicaciones h = new HistorialComunicaciones();
        h.setTipo("notificacion");
        h.setIdReferencia(saved.getIdNotificacion());
        h.setTitulo("Nueva notificación");
        h.setDescripcion(saved.getMensaje());
        h.setFecha(LocalDateTime.now());
        h.setIdUsuarioRemitente(userService.emailToUserId(auth.getName()));
        h.setIdUsuarioDestinatario(saved.getIdUsuario());
        h.setCanal(saved.getCanal());
        historialRepo.save(h);

        return saved;
    }

    /**
     * Obtiene las notificaciones de un usuario, validando permisos.
     */
    @PreAuthorize("hasAnyRole('ADMIN','EMISOR','DEVELOPER')")
    public List<Notificacion> findNotificationsForUser(Long idUsuario, Authentication auth) {
        if (!permService.isAdmin(auth)
                && !permService.isOwner(auth, idUsuario)
                && !permService.isDeveloper(auth)) {
            throw new AccessDeniedException("No autorizado");
        }
        return notificacionRepo.findByIdUsuarioOrderByFechaDesc(idUsuario);
    }

    /**
     * Envía un correo masivo (solo Admin o Emisor autorizado).
     * ⚠️ Lógica simplificada: deberías integrar un servicio asíncrono o cola.
     */
    @PreAuthorize("hasAnyRole('ADMIN','EMISOR')")
    public void sendMassEmail(String subject, String message, Long idEquipo, Authentication auth) {
        if (!permService.canSendMassEmail(auth, idEquipo) && !permService.isAdmin(auth)) {
            throw new AccessDeniedException("No autorizado para enviar correos masivos");
        }

        // Aquí iría la lógica de crear registro en correos_masivos, etc.
        System.out.println("Correo masivo enviado a equipo " + idEquipo + " con asunto: " + subject);
    }
}
