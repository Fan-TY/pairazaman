package com.repo.pairazaman;

import com.repo.pairazaman.config.SecurityConfig;
import com.repo.pairazaman.controller.PlanServicioController;
import com.repo.pairazaman.model.PlanServicio;
import com.repo.pairazaman.model.TipoPlan;
import com.repo.pairazaman.service.PlanServicioService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PlanServicioController.class)
@Import(SecurityConfig.class)
class PlanServicioControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockitoBean
    private PlanServicioService planService;

    private PlanServicio combo() {
        PlanServicio p = new PlanServicio("Combo Hogar", TipoPlan.COMBO, 100, 90,
                new BigDecimal("95.00"), new BigDecimal("100.00"), "Internet + TV");
        p.setId(1L);
        return p;
    }

    @Test
    @WithMockUser(roles = "GERENTE")
    void gerenteVeElCatalogo() throws Exception {
        when(planService.listar(null)).thenReturn(List.of(combo()));
        when(planService.contarActivos()).thenReturn(1L);
        mvc.perform(get("/planes"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Combo Hogar")))
                .andExpect(content().string(containsString("S/ 95.00")))
                .andExpect(content().string(containsString("100 Mbps")));
    }

    @Test
    @WithMockUser(roles = "GERENTE")
    void filtraPorTipo() throws Exception {
        when(planService.listar(TipoPlan.TV)).thenReturn(List.of());
        mvc.perform(get("/planes").param("tipo", "TV"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("No hay planes registrados")));
        verify(planService).listar(TipoPlan.TV);
    }

    @Test
    @WithMockUser(roles = "GERENTE")
    void muestraFormularioNuevoYEditar() throws Exception {
        mvc.perform(get("/planes/nuevo"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Nuevo plan")));
        when(planService.buscarPorId(1L)).thenReturn(java.util.Optional.of(combo()));
        mvc.perform(get("/planes/editar/1"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Editar plan")))
                .andExpect(content().string(containsString("Combo Hogar")));
    }

    @Test
    @WithMockUser(roles = "GERENTE")
    void guardarRedirigeAlCatalogo() throws Exception {
        when(planService.guardar(any())).thenAnswer(inv -> inv.getArgument(0));
        mvc.perform(post("/planes/guardar").with(csrf())
                        .param("nombre", "Internet 50")
                        .param("tipo", "INTERNET")
                        .param("velocidadMbps", "50")
                        .param("precioMensual", "50.00")
                        .param("costoInstalacion", "80"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/planes"));
    }

    @Test
    @WithMockUser(roles = "GERENTE")
    void errorDeValidacionVuelveAlFormulario() throws Exception {
        when(planService.guardar(any())).thenThrow(new IllegalArgumentException("El precio mensual debe ser mayor a 0."));
        mvc.perform(post("/planes/guardar").with(csrf())
                        .param("nombre", "X").param("tipo", "TV").param("precioMensual", "0"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("El precio mensual debe ser mayor a 0.")));
    }

    @Test
    @WithMockUser(roles = "SECRETARIA")
    void otrosRolesNoAcceden() throws Exception {
        mvc.perform(get("/planes"))
                .andExpect(status().isForbidden());
    }
}
