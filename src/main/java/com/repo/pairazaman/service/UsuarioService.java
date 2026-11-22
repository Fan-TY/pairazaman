package com.repo.pairazaman.service;

import com.repo.pairazaman.dto.UsuarioFormDto;
import com.repo.pairazaman.model.Usuario;

import java.util.List;
import java.util.Optional;

public interface UsuarioService {

    List<Usuario> listarTodos();

    Optional<Usuario> buscarPorId(Long id);

    Optional<Usuario> buscarPorUsername(String username);

    Usuario crear(UsuarioFormDto dto);

    Usuario actualizar(UsuarioFormDto dto, String usernameActual);

    void cambiarEstado(Long id, String usernameActual);

    void restablecerPassword(Long id, String nuevaPassword);

    void cambiarPassword(String username, String actual, String nueva);

    boolean existePorUsername(String username);

    boolean existePorEmail(String email);
}