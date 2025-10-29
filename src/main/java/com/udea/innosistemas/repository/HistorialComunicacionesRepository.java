package com.udea.innosistemas.repository;

import com.udea.innosistemas.entity.HistorialComunicaciones;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HistorialComunicacionesRepository extends JpaRepository<HistorialComunicaciones, Long> {
    List<HistorialComunicaciones> findByIdUsuarioRemitente(Long idUsuarioRemitente);
    List<HistorialComunicaciones> findByIdUsuarioDestinatario(Long idUsuarioDestinatario);
}
