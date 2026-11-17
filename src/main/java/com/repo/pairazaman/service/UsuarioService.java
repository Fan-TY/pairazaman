package com.repo.pairazaman.service;

import com.repo.pairazaman.dto.UsuarioRegistroDto;
import com.repo.pairazaman.model.Usuario;

import java.util.List;
import java.util.Optional;

public interface UsuarioService {

    List<Usuario> listarTodos();

    Optional<Usuario> buscarPorId(Long id);

    Optional<Usuario> buscarPorUsername(String username);

    Usuario registrar(UsuarioRegistroDto dto);

    boolean existePorUsername(String username);

    boolean existePorEmail(String email);
}
