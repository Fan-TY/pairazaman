package com.repo.pairazaman.repository;

import com.repo.pairazaman.model.PlanServicio;
import com.repo.pairazaman.model.TipoPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlanServicioRepository extends JpaRepository<PlanServicio, Long> {

    List<PlanServicio> findAllByOrderByPrecioMensualAsc();

    List<PlanServicio> findByTipoOrderByPrecioMensualAsc(TipoPlan tipo);

    List<PlanServicio> findByActivoTrueOrderByPrecioMensualAsc();

    boolean existsByNombreIgnoreCase(String nombre);

    boolean existsByNombreIgnoreCaseAndIdNot(String nombre, Long id);

    long countByActivoTrue();
}
