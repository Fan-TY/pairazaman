package com.repo.pairazaman.service.impl;

import com.repo.pairazaman.dto.UsuarioFormDto;
import com.repo.pairazaman.model.Rol;
import com.repo.pairazaman.model.Tecnico;
import com.repo.pairazaman.model.Usuario;
import com.repo.pairazaman.repository.UsuarioRepository;
import com.repo.pairazaman.service.UsuarioService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UsuarioServiceImpl implements UsuarioService {

    private static final int LARGO_MINIMO_PASSWORD = 6;

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioServiceImpl(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Usuario> listarTodos() {
        return usuarioRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Usuario> buscarPorId(Long id) {
        return usuarioRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Usuario> buscarPorUsername(String username) {
        return usuarioRepository.findByUsername(username);
    }

    @Override
    public Usuario crear(UsuarioFormDto dto) {
        String username = limpiar(dto.getUsername());
        String nombre = limpiar(dto.getNombreCompleto());
        String email = limpiar(dto.getEmail());

        if (username.isEmpty()) {
            throw new IllegalArgumentException("El nombre de usuario es obligatorio.");
        }
        if (nombre.isEmpty()) {
            throw new IllegalArgumentException("El nombre completo es obligatorio.");
        }
        if (email.isEmpty()) {
            throw new IllegalArgumentException("El correo electrónico es obligatorio.");
        }
        if (dto.getRol() == null) {
            throw new IllegalArgumentException("Selecciona un rol.");
        }
        if (usuarioRepository.existsByUsername(username)) {
            throw new IllegalArgumentException("El usuario ya se encuentra registrado.");
        }
        if (usuarioRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("El correo electrónico ya está en uso.");
        }
        validarPassword(dto.getPassword());
        if (!dto.getPassword().equals(dto.getConfirmarPassword())) {
            throw new IllegalArgumentException("Las contraseñas no coinciden.");
        }

        Usuario usuario = new Usuario(username, passwordEncoder.encode(dto.getPassword()), nombre, email, dto.getRol());
        sincronizarTecnico(usuario, dto.getTelefono());
        return usuarioRepository.save(usuario);
    }

    @Override
    public Usuario actualizar(UsuarioFormDto dto, String usernameActual) {
        Usuario usuario = usuarioRepository.findById(dto.getId())
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado."));

        String nombre = limpiar(dto.getNombreCompleto());
        String email = limpiar(dto.getEmail());

        if (nombre.isEmpty()) {
            throw new IllegalArgumentException("El nombre completo es obligatorio.");
        }
        if (email.isEmpty()) {
            throw new IllegalArgumentException("El correo electrónico es obligatorio.");
        }
        if (dto.getRol() == null) {
            throw new IllegalArgumentException("Selecciona un rol.");
        }
        if (usuario.getUsername().equals(usernameActual) && dto.getRol() != Rol.ROLE_GERENTE) {
            throw new IllegalArgumentException("No puedes quitarte tu propio rol de Gerente.");
        }
        boolean correoOcupado = usuarioRepository.findByEmail(email)
                .filter(otro -> !otro.getId().equals(usuario.getId()))
                .isPresent();
        if (correoOcupado) {
            throw new IllegalArgumentException("El correo electrónico ya está en uso.");
        }

        usuario.setNombreCompleto(nombre);
        usuario.setEmail(email);
        usuario.setRol(dto.getRol());
        sincronizarTecnico(usuario, dto.getTelefono());
        return usuarioRepository.save(usuario);
    }

    @Override
    public void cambiarEstado(Long id, String usernameActual) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado."));
        if (usuario.getUsername().equals(usernameActual)) {
            throw new IllegalArgumentException("No puedes desactivar tu propia cuenta.");
        }
        usuario.setActivo(!usuario.isActivo());
        if (usuario.getTecnico() != null) {
            usuario.getTecnico().setActivo(usuario.isActivo());
        }
        usuarioRepository.save(usuario);
    }

    @Override
    public void restablecerPassword(Long id, String nuevaPassword) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado."));
        validarPassword(nuevaPassword);
        usuario.setPassword(passwordEncoder.encode(nuevaPassword));
        usuarioRepository.save(usuario);
    }

    @Override
    public void cambiarPassword(String username, String actual, String nueva) {
        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado."));
        if (actual == null || !passwordEncoder.matches(actual, usuario.getPassword())) {
            throw new IllegalArgumentException("La contraseña actual no es correcta.");
        }
        validarPassword(nueva);
        usuario.setPassword(passwordEncoder.encode(nueva));
        usuarioRepository.save(usuario);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existePorUsername(String username) {
        return usuarioRepository.existsByUsername(username);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existePorEmail(String email) {
        return usuarioRepository.existsByEmail(email);
    }

    private void sincronizarTecnico(Usuario usuario, String telefono) {
        if (usuario.getRol() == Rol.ROLE_TECNICO) {
            Tecnico tecnico = usuario.getTecnico() != null ? usuario.getTecnico() : new Tecnico();
            tecnico.setNombreCompleto(usuario.getNombreCompleto());
            tecnico.setTelefono(limpiar(telefono));
            tecnico.setActivo(usuario.isActivo());
            usuario.setTecnico(tecnico);
        } else if (usuario.getTecnico() != null) {
            usuario.getTecnico().setActivo(false);
            usuario.setTecnico(null);
        }
    }

    private void validarPassword(String password) {
        if (password == null || password.length() < LARGO_MINIMO_PASSWORD) {
            throw new IllegalArgumentException("La contraseña debe tener al menos " + LARGO_MINIMO_PASSWORD + " caracteres.");
        }
    }

    private String limpiar(String valor) {
        return valor == null ? "" : valor.trim();
    }
}