package com.udea.innosistemas.repository;

import com.udea.innosistemas.entity.Alerta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AlertaRepository extends JpaRepository<Alerta, Long> {
    List<Alerta> findByIdUsuario(Long idUsuario);
    List<Alerta> findByLeida(Boolean leida);
}
