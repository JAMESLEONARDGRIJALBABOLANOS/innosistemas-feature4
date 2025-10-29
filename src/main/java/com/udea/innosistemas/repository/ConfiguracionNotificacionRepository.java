package com.udea.innosistemas.repository;

import com.udea.innosistemas.entity.ConfiguracionNotificacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ConfiguracionNotificacionRepository extends JpaRepository<ConfiguracionNotificacion, Long> {
    Optional<ConfiguracionNotificacion> findByIdUsuario(Long idUsuario);
}
