package com.repo.pairazaman.repository;

import com.repo.pairazaman.model.Tecnico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TecnicoRepository extends JpaRepository<Tecnico, Long> {

    List<Tecnico> findAllByOrderByNombreCompletoAsc();

    List<Tecnico> findByActivoTrueOrderByNombreCompletoAsc();
}
