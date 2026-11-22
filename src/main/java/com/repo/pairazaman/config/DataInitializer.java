package com.repo.pairazaman.config;

import com.repo.pairazaman.model.Rol;
import com.repo.pairazaman.model.Tecnico;
import com.repo.pairazaman.model.Usuario;
import com.repo.pairazaman.repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (usuarioRepository.count() > 0) {
            return;
        }

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
}