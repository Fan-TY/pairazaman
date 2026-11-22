package com.repo.pairazaman;

import com.repo.pairazaman.model.PlanServicio;
import com.repo.pairazaman.model.TipoPlan;
import com.repo.pairazaman.repository.PlanServicioRepository;
import com.repo.pairazaman.service.impl.PlanServicioServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class PlanServicioServiceImplTest {

    private PlanServicioRepository repo;
    private PlanServicioServiceImpl service;

    @BeforeEach
    void setUp() {
        repo = mock(PlanServicioRepository.class);
        service = new PlanServicioServiceImpl(repo);
        when(repo.save(any(PlanServicio.class))).thenAnswer(inv -> inv.getArgument(0));
    }

    private PlanServicio plan(TipoPlan tipo, Integer mbps, Integer canales) {
        PlanServicio p = new PlanServicio();
        p.setNombre("  Plan Prueba  ");
        p.setTipo(tipo);
        p.setVelocidadMbps(mbps);
        p.setCantidadCanales(canales);
        p.setPrecioMensual(new BigDecimal("70.00"));
        p.setCostoInstalacion(new BigDecimal("80.00"));
        return p;
    }

    @Test
    void registraPlanDeInternetActivoYSinCanales() {
        PlanServicio p = service.guardar(plan(TipoPlan.INTERNET, 100, 50));
        assertEquals("Plan Prueba", p.getNombre());
        assertEquals(100, p.getVelocidadMbps());
        assertNull(p.getCantidadCanales());
        assertTrue(p.isActivo());
    }

    @Test
    void comboExigeVelocidadYCanales() {
        assertThrows(IllegalArgumentException.class, () -> service.guardar(plan(TipoPlan.COMBO, 100, null)));
        assertThrows(IllegalArgumentException.class, () -> service.guardar(plan(TipoPlan.COMBO, null, 80)));
        PlanServicio ok = service.guardar(plan(TipoPlan.COMBO, 100, 80));
        assertEquals(100, ok.getVelocidadMbps());
        assertEquals(80, ok.getCantidadCanales());
    }

    @Test
    void planDeTvNoGuardaVelocidad() {
        PlanServicio p = service.guardar(plan(TipoPlan.TV, 200, 70));
        assertNull(p.getVelocidadMbps());
        assertEquals(70, p.getCantidadCanales());
    }

    @Test
    void rechazaPrecioCeroONegativo() {
        PlanServicio p = plan(TipoPlan.INTERNET, 50, null);
        p.setPrecioMensual(BigDecimal.ZERO);
        assertThrows(IllegalArgumentException.class, () -> service.guardar(p));
        p.setPrecioMensual(new BigDecimal("50"));
        p.setCostoInstalacion(new BigDecimal("-1"));
        assertThrows(IllegalArgumentException.class, () -> service.guardar(p));
        verify(repo, never()).save(any());
    }

    @Test
    void costoInstalacionVacioQuedaEnCero() {
        PlanServicio p = plan(TipoPlan.INTERNET, 50, null);
        p.setCostoInstalacion(null);
        assertEquals(0, BigDecimal.ZERO.compareTo(service.guardar(p).getCostoInstalacion()));
    }

    @Test
    void rechazaNombreDuplicado() {
        when(repo.existsByNombreIgnoreCase("Plan Prueba")).thenReturn(true);
        assertThrows(IllegalArgumentException.class, () -> service.guardar(plan(TipoPlan.INTERNET, 50, null)));
        verify(repo, never()).save(any());
    }

    @Test
    void editarConservaEstadoYPermiteMismoNombre() {
        PlanServicio existente = plan(TipoPlan.INTERNET, 50, null);
        existente.setId(5L);
        existente.setActivo(false);
        when(repo.findById(5L)).thenReturn(Optional.of(existente));
        when(repo.existsByNombreIgnoreCaseAndIdNot("Plan Prueba", 5L)).thenReturn(false);

        PlanServicio cambios = plan(TipoPlan.INTERNET, 300, null);
        cambios.setId(5L);
        cambios.setActivo(true);
        PlanServicio guardado = service.guardar(cambios);

        assertSame(existente, guardado);
        assertEquals(300, guardado.getVelocidadMbps());
        assertFalse(guardado.isActivo());
    }

    @Test
    void cambiarEstadoAlternaActivo() {
        PlanServicio p = plan(TipoPlan.TV, null, 60);
        when(repo.findById(1L)).thenReturn(Optional.of(p));
        assertFalse(service.cambiarEstado(1L).isActivo());
        assertTrue(service.cambiarEstado(1L).isActivo());
    }

    @Test
    void cambiarEstadoDePlanInexistenteFalla() {
        when(repo.findById(99L)).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class, () -> service.cambiarEstado(99L));
    }
}
