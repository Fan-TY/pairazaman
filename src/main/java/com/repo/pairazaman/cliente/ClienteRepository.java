package com.repo.pairazaman.cliente;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio de clientes. Los métodos derivados cubren el filtrado dinámico
 * por nombre y estado exigido por el GET /api/clientes.
 */
public interface ClienteRepository extends JpaRepository<Cliente, Long> {

    boolean existsByDniRuc(String dniRuc);

    Optional<Cliente> findByDniRuc(String dniRuc);

    List<Cliente> findByNombresRazonSocialContainingIgnoreCase(String nombre, Sort sort);

    List<Cliente> findByEstado(EstadoCliente estado, Sort sort);

    List<Cliente> findByNombresRazonSocialContainingIgnoreCaseAndEstado(
            String nombre, EstadoCliente estado, Sort sort);
}
