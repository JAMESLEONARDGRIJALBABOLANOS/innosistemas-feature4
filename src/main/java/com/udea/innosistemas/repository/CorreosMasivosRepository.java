package com.udea.innosistemas.repository;

import com.udea.innosistemas.entity.CorreosMasivos;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CorreosMasivosRepository extends JpaRepository<CorreosMasivos, Long> {
    List<CorreosMasivos> findByIdUsuario(Long idUsuario);
    List<CorreosMasivos> findByEstado(String estado);
}
