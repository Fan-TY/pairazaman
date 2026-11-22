package com.repo.pairazaman.repository;

import com.repo.pairazaman.model.EstadoIncidencia;
import com.repo.pairazaman.model.Incidencia;
import com.repo.pairazaman.model.Prioridad;
import com.repo.pairazaman.model.TipoIncidencia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IncidenciaRepository extends JpaRepository<Incidencia, Long> {

    List<Incidencia> findByEstado(EstadoIncidencia estado);

    List<Incidencia> findByPrioridad(Prioridad prioridad);

    List<Incidencia> findByTipo(TipoIncidencia tipo);

    long countByEstado(EstadoIncidencia estado);

    List<Incidencia> findTop5ByOrderByFechaRegistroDesc();

    List<Incidencia> findAllByOrderByFechaDescHoraDesc();

    @Query("SELECT i FROM Incidencia i WHERE " +
           "(:estado IS NULL OR i.estado = :estado) AND " +
           "(:tipo IS NULL OR i.tipo = :tipo) " +
           "ORDER BY i.fecha DESC, i.hora DESC")
    List<Incidencia> filtrar(@Param("estado") EstadoIncidencia estado,
                             @Param("tipo") TipoIncidencia tipo);
}
