package com.repo.pairazaman.service.impl;

import com.repo.pairazaman.dto.UsuarioRegistroDto;
import com.repo.pairazaman.model.Rol;
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
    public Usuario registrar(UsuarioRegistroDto dto) {
        if (dto.getUsername() == null || dto.getUsername().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre de usuario es obligatorio.");
        }
        if (usuarioRepository.existsByUsername(dto.getUsername().trim())) {
            throw new IllegalArgumentException("El usuario ya se encuentra registrado.");
        }
        if (dto.getEmail() != null && usuarioRepository.existsByEmail(dto.getEmail().trim())) {
            throw new IllegalArgumentException("El correo electrónico ya está en uso.");
        }

        Usuario usuario = new Usuario();
        usuario.setUsername(dto.getUsername().trim());
        usuario.setPassword(passwordEncoder.encode(dto.getPassword()));
        usuario.setNombreCompleto(dto.getNombreCompleto());
        usuario.setEmail(dto.getEmail());
        usuario.setRol(dto.getRol() != null ? dto.getRol() : Rol.ROLE_SECRETARIA);
        usuario.setActivo(true);

        return usuarioRepository.save(usuario);
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
}