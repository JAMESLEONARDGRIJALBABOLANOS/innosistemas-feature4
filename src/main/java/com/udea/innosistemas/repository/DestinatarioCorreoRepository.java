package com.udea.innosistemas.repository;

import com.udea.innosistemas.entity.DestinatarioCorreo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DestinatarioCorreoRepository extends JpaRepository<DestinatarioCorreo, Long> {
    List<DestinatarioCorreo> findByIdCorreo(Long idCorreo);
    List<DestinatarioCorreo> findByIdUsuario(Long idUsuario);
}
