package com.repo.pairazaman.historial;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface PagoRepository extends JpaRepository<Pago, Long> {

    List<Pago> findAllByOrderByIdDesc();

    List<Pago> findByEstadoIgnoreCaseOrderByIdDesc(String estado);

    List<Pago> findByClienteIdOrderByIdDesc(Long clienteId);

    List<Pago> findByClienteIdAndEstadoInOrderByFechaVencimientoAsc(Long clienteId, List<String> estados);

    @Query("SELECT COALESCE(SUM(p.monto), 0) FROM Pago p WHERE p.cliente.id = :clienteId AND UPPER(p.estado) IN ('PENDIENTE', 'VENCIDO')")
    BigDecimal calcularDeudaCliente(@Param("clienteId") Long clienteId);

    @Query("SELECT p FROM Pago p JOIN FETCH p.cliente c WHERE UPPER(p.estado) = 'VENCIDO' ORDER BY p.fechaVencimiento ASC, p.id DESC")
    List<Pago> listarMorosos();

    @Query("SELECT p FROM Pago p JOIN FETCH p.cliente c " +
           "WHERE (:estado IS NULL OR :estado = '' OR UPPER(p.estado) = UPPER(:estado)) " +
           "AND (:filtro IS NULL OR :filtro = '' OR LOWER(c.nombresRazonSocial) LIKE LOWER(CONCAT('%', :filtro, '%')) " +
           "OR LOWER(c.dniRuc) LIKE LOWER(CONCAT('%', :filtro, '%')) " +
           "OR LOWER(COALESCE(p.numeroComprobante, '')) LIKE LOWER(CONCAT('%', :filtro, '%'))) " +
           "ORDER BY p.id DESC")
    List<Pago> buscarConFiltros(@Param("estado") String estado, @Param("filtro") String filtro);
}
