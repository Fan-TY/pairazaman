package com.repo.pairazaman.config;

import com.repo.pairazaman.dto.IncidenciaDto;
import com.repo.pairazaman.model.*;
import com.repo.pairazaman.repository.CamaraRepository;
import com.repo.pairazaman.repository.IncidenciaRepository;
import com.repo.pairazaman.repository.UsuarioRepository;
import com.repo.pairazaman.service.IncidenciaService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final CamaraRepository camaraRepository;
    private final IncidenciaRepository incidenciaRepository;
    private final IncidenciaService incidenciaService;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UsuarioRepository usuarioRepository,
                           CamaraRepository camaraRepository,
                           IncidenciaRepository incidenciaRepository,
                           IncidenciaService incidenciaService,
                           PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.camaraRepository = camaraRepository;
        this.incidenciaRepository = incidenciaRepository;
        this.incidenciaService = incidenciaService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        if (usuarioRepository.count() == 0) {
            usuarioRepository.save(new Usuario(
                    "gerente",
                    passwordEncoder.encode("gerente123"),
                    "Gerente General",
                    "gerente@pairazaman.com",
                    Rol.ROLE_GERENTE
            ));
            usuarioRepository.save(new Usuario(
                    "secretaria",
                    passwordEncoder.encode("secretaria123"),
                    "Secretaria",
                    "secretaria@pairazaman.com",
                    Rol.ROLE_SECRETARIA
            ));
            usuarioRepository.save(new Usuario(
                    "cobranza",
                    passwordEncoder.encode("cobranza123"),
                    "Encargado de Cobranza",
                    "cobranza@pairazaman.com",
                    Rol.ROLE_COBRANZA
            ));
            Usuario tecnico = new Usuario(
                    "tecnico",
                    passwordEncoder.encode("tecnico123"),
                    "Técnico de Campo",
                    "tecnico@pairazaman.com",
                    Rol.ROLE_TECNICO
            );
            tecnico.setTecnico(new Tecnico("Técnico de Campo", "999000000"));
            usuarioRepository.save(tecnico);
        }

        if (camaraRepository.count() == 0) {
            camaraRepository.save(new Camara("CAM-01", "Acceso Principal - Puerta A", "192.168.1.101", "OPERATIVA"));
            camaraRepository.save(new Camara("CAM-02", "Estacionamiento Norte", "192.168.1.102", "OPERATIVA"));
            camaraRepository.save(new Camara("CAM-03", "Pasillo Central - Nivel 2", "192.168.1.103", "MANTENIMIENTO"));
            camaraRepository.save(new Camara("CAM-04", "Almacén General y Carga", "192.168.1.104", "OPERATIVA"));
            camaraRepository.save(new Camara("CAM-05", "Perímetro Posterior", "192.168.1.105", "DESCONECTADA"));
        }

        if (incidenciaRepository.count() == 0) {
            Usuario gerente = usuarioRepository.findByUsername("gerente").orElse(null);
            Camara cam1 = camaraRepository.findByCodigo("CAM-01").orElse(null);
            Camara cam2 = camaraRepository.findByCodigo("CAM-02").orElse(null);

            IncidenciaDto inc1 = new IncidenciaDto();
            inc1.setTitulo("Persona no autorizada en acceso principal");
            inc1.setDescripcion("Individuo intentando ingresar fuera de horario por la puerta A.");
            inc1.setFecha(LocalDate.now());
            inc1.setHora("14:30");
            inc1.setTipo(TipoIncidencia.SOSPECHOSO);
            inc1.setPrioridad(Prioridad.ALTA);
            inc1.setEstado(EstadoIncidencia.PENDIENTE);
            inc1.setUbicacion("Puerta A - Garita Principal");
            if (cam1 != null) inc1.setCamaraId(cam1.getId());
            incidenciaService.registrar(inc1, gerente);

            IncidenciaDto inc2 = new IncidenciaDto();
            inc2.setTitulo("Avería en luminaria de estacionamiento");
            inc2.setDescripcion("Corte de suministro en farola norte, poca visibilidad para cámaras.");
            inc2.setFecha(LocalDate.now().minusDays(1));
            inc2.setHora("20:15");
            inc2.setTipo(TipoIncidencia.INFRAESTRUCTURA);
            inc2.setPrioridad(Prioridad.MEDIA);
            inc2.setEstado(EstadoIncidencia.EN_PROCESO);
            inc2.setUbicacion("Estacionamiento Norte");
            if (cam2 != null) inc2.setCamaraId(cam2.getId());
            incidenciaService.registrar(inc2, gerente);

            IncidenciaDto inc3 = new IncidenciaDto();
            inc3.setTitulo("Mochila olvidada en banco de espera");
            inc3.setDescripcion("Inspeccionada por personal de seguridad y devuelta a su dueño.");
            inc3.setFecha(LocalDate.now().minusDays(2));
            inc3.setHora("11:00");
            inc3.setTipo(TipoIncidencia.OTRO);
            inc3.setPrioridad(Prioridad.BAJA);
            inc3.setEstado(EstadoIncidencia.RESUELTO);
            inc3.setUbicacion("Hall de Espera Principal");
            incidenciaService.registrar(inc3, gerente);
        }
    }
}