package com.repo.pairazaman.cliente;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ClienteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ClienteRepository clienteRepository;

    @BeforeEach
    void limpiarBase() {
        clienteRepository.deleteAll();
    }

    @Test
    void registraClienteYLoFiltraPorNombreYEstado() throws Exception {
        mockMvc.perform(post("/api/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "dniRuc": "0102030405",
                                  "nombresRazonSocial": "María Pairazaman",
                                  "direccion": "Av. Principal 123",
                                  "telefono": "0999999999",
                                  "correo": "maria@example.com",
                                  "tipo": "HOGAR"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.estado").value("ACTIVO"))
                .andExpect(jsonPath("$.tipo").value("HOGAR"));

        mockMvc.perform(get("/api/clientes").param("nombre", "maría"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].dniRuc").value("0102030405"));

        mockMvc.perform(get("/api/clientes").param("estado", "INACTIVO"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void cambiaEstadoYDevuelveHistorialVacio() throws Exception {
        Cliente cliente = new Cliente();
        cliente.setDniRuc("1799999999001");
        cliente.setNombresRazonSocial("Comercial Andina");
        cliente.setTipo(TipoCliente.COMERCIO);
        cliente = clienteRepository.save(cliente);

        mockMvc.perform(patch("/api/clientes/{id}/estado", cliente.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"estado\":\"INACTIVO\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("INACTIVO"));

        mockMvc.perform(get("/api/clientes/{id}/historial", cliente.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.dniRuc").value("1799999999001"))
                .andExpect(jsonPath("$.contratos.length()").value(0))
                .andExpect(jsonPath("$.pagos.length()").value(0))
                .andExpect(jsonPath("$.reclamos.length()").value(0))
                .andExpect(jsonPath("$.instalaciones.length()").value(0));
    }

    @Test
    void rechazaDniInvalidoYClienteInexistente() throws Exception {
        mockMvc.perform(post("/api/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"dniRuc":"123","nombresRazonSocial":"Prueba","tipo":"HOGAR"}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.campos.dniRuc").exists());

        mockMvc.perform(get("/api/clientes/{id}", 9999))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.mensaje").exists());
    }
}
