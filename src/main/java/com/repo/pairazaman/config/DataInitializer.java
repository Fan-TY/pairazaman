package com.repo.pairazaman.config;

import com.repo.pairazaman.model.PlanServicio;
import com.repo.pairazaman.model.Rol;
import com.repo.pairazaman.model.Tecnico;
import com.repo.pairazaman.model.TipoPlan;
import com.repo.pairazaman.model.Usuario;
import com.repo.pairazaman.repository.PlanServicioRepository;
import com.repo.pairazaman.repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final PlanServicioRepository planRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UsuarioRepository usuarioRepository,
                           PlanServicioRepository planRepository,
                           PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.planRepository = planRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (usuarioRepository.count() == 0) {
            crearUsuarios();
        }
        if (planRepository.count() == 0) {
            crearPlanes();
        }
    }

    private void crearUsuarios() {
        crear("gerente", "gerente123", "Gerente General", "gerente@pairazaman.com", Rol.ROLE_GERENTE, null);
        crear("secretaria", "secretaria123", "Secretaria", "secretaria@pairazaman.com", Rol.ROLE_SECRETARIA, null);
        crear("cobranza", "cobranza123", "Encargado de Cobranza", "cobranza@pairazaman.com", Rol.ROLE_COBRANZA, null);
        crear("tecnico", "tecnico123", "Técnico de Campo", "tecnico@pairazaman.com", Rol.ROLE_TECNICO,
                new Tecnico("Técnico de Campo", "999000000"));
    }

    private void crear(String username, String password, String nombre, String email, Rol rol, Tecnico tecnico) {
        Usuario usuario = new Usuario(username, passwordEncoder.encode(password), nombre, email, rol);
        usuario.setTecnico(tecnico);
        usuarioRepository.save(usuario);
    }

    private void crearPlanes() {
        plan("Internet Hogar 50 Mbps", TipoPlan.INTERNET, 50, null, "50.00", "80.00",
                "Fibra óptica para uso doméstico.");
        plan("Internet Hogar 100 Mbps", TipoPlan.INTERNET, 100, null, "70.00", "80.00",
                "Fibra óptica, ideal para clases virtuales y streaming.");
        plan("Internet Negocio 200 Mbps", TipoPlan.INTERNET, 200, null, "120.00", "100.00",
                "Para comercios e instituciones educativas.");
        plan("Cable TV Básico", TipoPlan.TV, null, 70, "35.00", "50.00",
                "Canales nacionales y principales señales de cable.");
        plan("Combo Hogar 100 Mbps + TV", TipoPlan.COMBO, 100, 90, "95.00", "100.00",
                "Internet de fibra y televisión por cable en un solo pago.");
    }

    private void plan(String nombre, TipoPlan tipo, Integer mbps, Integer canales,
                      String mensual, String instalacion, String descripcion) {
        planRepository.save(new PlanServicio(nombre, tipo, mbps, canales,
                new BigDecimal(mensual), new BigDecimal(instalacion), descripcion));
    }
}
