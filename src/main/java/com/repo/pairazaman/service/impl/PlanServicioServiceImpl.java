package com.repo.pairazaman.service.impl;

import com.repo.pairazaman.model.PlanServicio;
import com.repo.pairazaman.model.TipoPlan;
import com.repo.pairazaman.repository.PlanServicioRepository;
import com.repo.pairazaman.service.PlanServicioService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class PlanServicioServiceImpl implements PlanServicioService {

    private final PlanServicioRepository planRepository;

    public PlanServicioServiceImpl(PlanServicioRepository planRepository) {
        this.planRepository = planRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<PlanServicio> listar(TipoPlan tipo) {
        if (tipo == null) {
            return planRepository.findAllByOrderByPrecioMensualAsc();
        }
        return planRepository.findByTipoOrderByPrecioMensualAsc(tipo);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PlanServicio> listarActivos() {
        return planRepository.findByActivoTrueOrderByPrecioMensualAsc();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<PlanServicio> buscarPorId(Long id) {
        return planRepository.findById(id);
    }

    @Override
    public PlanServicio guardar(PlanServicio datos) {
        validar(datos);

        PlanServicio plan = new PlanServicio();
        if (datos.getId() != null) {
            plan = planRepository.findById(datos.getId())
                    .orElseThrow(() -> new IllegalArgumentException("Plan no encontrado."));
        }

        TipoPlan tipo = datos.getTipo();
        plan.setNombre(datos.getNombre().trim());
        plan.setTipo(tipo);
        plan.setVelocidadMbps(tipo.tieneInternet() ? datos.getVelocidadMbps() : null);
        plan.setCantidadCanales(tipo.tieneTv() ? datos.getCantidadCanales() : null);
        plan.setPrecioMensual(datos.getPrecioMensual());
        plan.setCostoInstalacion(datos.getCostoInstalacion() != null ? datos.getCostoInstalacion() : BigDecimal.ZERO);
        plan.setDescripcion(datos.getDescripcion() != null && !datos.getDescripcion().isBlank()
                ? datos.getDescripcion().trim() : null);

        return planRepository.save(plan);
    }

    private void validar(PlanServicio plan) {
        if (plan.getNombre() == null || plan.getNombre().isBlank()) {
            throw new IllegalArgumentException("El nombre del plan es obligatorio.");
        }
        if (plan.getTipo() == null) {
            throw new IllegalArgumentException("Selecciona el tipo de plan.");
        }

        String nombre = plan.getNombre().trim();
        boolean repetido = plan.getId() == null
                ? planRepository.existsByNombreIgnoreCase(nombre)
                : planRepository.existsByNombreIgnoreCaseAndIdNot(nombre, plan.getId());
        if (repetido) {
            throw new IllegalArgumentException("Ya existe un plan llamado \"" + nombre + "\".");
        }

        if (plan.getPrecioMensual() == null || plan.getPrecioMensual().signum() <= 0) {
            throw new IllegalArgumentException("El precio mensual debe ser mayor a 0.");
        }
        if (plan.getCostoInstalacion() != null && plan.getCostoInstalacion().signum() < 0) {
            throw new IllegalArgumentException("El costo de instalación no puede ser negativo.");
        }
        if (plan.getTipo().tieneInternet() && (plan.getVelocidadMbps() == null || plan.getVelocidadMbps() <= 0)) {
            throw new IllegalArgumentException("Ingresa la velocidad en Mbps.");
        }
        if (plan.getTipo().tieneTv() && (plan.getCantidadCanales() == null || plan.getCantidadCanales() <= 0)) {
            throw new IllegalArgumentException("Ingresa la cantidad de canales.");
        }
    }

    @Override
    public PlanServicio cambiarEstado(Long id) {
        PlanServicio plan = planRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Plan no encontrado."));
        plan.setActivo(!plan.isActivo());
        return planRepository.save(plan);
    }

    @Override
    @Transactional(readOnly = true)
    public long contarActivos() {
        return planRepository.countByActivoTrue();
    }
}
