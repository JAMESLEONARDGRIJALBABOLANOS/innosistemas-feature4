package com.udea.innosistemas.repository;

import com.udea.innosistemas.entity.Notificacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificacionRepository extends JpaRepository<Notificacion, Long> {

    // Notificaciones por usuario
    List<Notificacion> findByIdUsuario(Long idUsuario);

    // Notificaciones ordenadas por fecha descendente
    List<Notificacion> findByIdUsuarioOrderByFechaDesc(Long idUsuario);

    // Buscar notificaciones por estado
    List<Notificacion> findByEstado(String estado);
}
