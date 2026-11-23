package com.repo.pairazaman.repository;

import com.repo.pairazaman.model.Notificacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificacionRepository extends JpaRepository<Notificacion, Long> {

    List<Notificacion> findByTecnicoIdAndLeidaFalseOrderByFechaDesc(Long tecnicoId);
}
