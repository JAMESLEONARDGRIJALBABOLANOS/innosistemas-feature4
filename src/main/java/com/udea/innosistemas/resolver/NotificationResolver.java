package com.udea.innosistemas.resolver;

import com.udea.innosistemas.dto.SendNotificationInput;
import com.udea.innosistemas.dto.ConfigInput;
import com.udea.innosistemas.entity.Notificacion;
import com.udea.innosistemas.entity.ConfiguracionNotificacion;
import com.udea.innosistemas.service.NotificationService;
import com.udea.innosistemas.service.PermissionService;
import com.udea.innosistemas.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
public class NotificationResolver {

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private PermissionService permService;

    @Autowired
    private UserService userService;

    @QueryMapping
    public ConfiguracionNotificacion myNotificationConfig(Authentication auth) {
        Long myId = userService.emailToUserId(auth.getName());
        return notificationService.getConfigByUserId(myId);
    }

    @MutationMapping
    public ConfiguracionNotificacion saveNotificationConfig(@Argument ConfigInput input, Authentication auth) {
        Long myId = userService.emailToUserId(auth.getName());
        return notificationService.saveOrUpdateConfig(myId, input);
    }

    @QueryMapping
    public List<Notificacion> myNotifications(Authentication auth) {
        Long myId = userService.emailToUserId(auth.getName());
        return notificationService.findNotificationsForUser(myId, auth);
    }

    @QueryMapping
    public List<Notificacion> notificationsForUser(@Argument Long idUsuario, Authentication auth) {
        if (!permService.isAdmin(auth) && !permService.isOwner(auth, idUsuario) && !permService.isDeveloper(auth)) {
            throw new org.springframework.security.access.AccessDeniedException("No autorizado");
        }
        return notificationService.findNotificationsForUser(idUsuario, auth);
    }

    @MutationMapping
    @PreAuthorize("hasAnyRole('EMISOR','ADMIN')")
    public Notificacion sendNotification(@Argument SendNotificationInput input, Authentication auth) {
        Long emitterId = userService.emailToUserId(auth.getName());
        Notificacion n = new Notificacion();
        n.setTipo(input.getTipo());
        n.setMensaje(input.getMensaje());
        n.setIdReferencia(input.getIdReferencia());
        n.setIdUsuario(input.getIdUsuario());
        n.setCanal(input.getCanal());
        n.setUrlDestino(input.getUrlDestino());
        return notificationService.sendNotification(n, auth);
    }
}
