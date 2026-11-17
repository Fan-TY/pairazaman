package com.repo.pairazaman.repository;

import com.repo.pairazaman.model.Camara;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CamaraRepository extends JpaRepository<Camara, Long> {

    Optional<Camara> findByCodigo(String codigo);

    List<Camara> findByEstado(String estado);

    long countByEstado(String estado);

    long countByActivaTrue();
}
