package com.repo.pairazaman.historial;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Repository
public interface InstalacionRepository extends JpaRepository<Instalacion, Long> {

    List<Instalacion> findAllByOrderByFechaInstalacionDescHoraDesc();

    List<Instalacion> findByFechaInstalacionBetweenOrderByFechaInstalacionAscHoraAsc(LocalDate desde, LocalDate hasta);

    /** Sirve para evitar que un técnico tenga dos órdenes el mismo día a la misma hora. */
    List<Instalacion> findByTecnicoIdAndFechaInstalacionAndHora(Long tecnicoId, LocalDate fecha, LocalTime hora);
}
