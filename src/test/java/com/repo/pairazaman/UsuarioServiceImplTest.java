package com.repo.pairazaman;

import com.repo.pairazaman.dto.UsuarioFormDto;
import com.repo.pairazaman.model.Rol;
import com.repo.pairazaman.model.Usuario;
import com.repo.pairazaman.repository.UsuarioRepository;
import com.repo.pairazaman.service.impl.UsuarioServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class UsuarioServiceImplTest {

    private UsuarioRepository repo;
    private PasswordEncoder encoder;
    private UsuarioServiceImpl service;

    @BeforeEach
    void setUp() {
        repo = mock(UsuarioRepository.class);
        encoder = new BCryptPasswordEncoder();
        service = new UsuarioServiceImpl(repo, encoder);
        when(repo.save(any(Usuario.class))).thenAnswer(inv -> inv.getArgument(0));
    }

    private UsuarioFormDto formValido(Rol rol) {
        UsuarioFormDto dto = new UsuarioFormDto();
        dto.setUsername("jperez");
        dto.setNombreCompleto("Juan Pérez");
        dto.setEmail("jperez@pairazaman.com");
        dto.setRol(rol);
        dto.setTelefono("987654321");
        dto.setPassword("clave123");
        dto.setConfirmarPassword("clave123");
        return dto;
    }

    @Test
    void creaUsuarioConPasswordEncriptada() {
        Usuario u = service.crear(formValido(Rol.ROLE_SECRETARIA));
        assertNotEquals("clave123", u.getPassword());
        assertTrue(encoder.matches("clave123", u.getPassword()));
        assertNull(u.getTecnico());
        assertTrue(u.isActivo());
    }

    @Test
    void usuarioTecnicoQuedaVinculadoAUnTecnico() {
        Usuario u = service.crear(formValido(Rol.ROLE_TECNICO));
        assertNotNull(u.getTecnico());
        assertEquals("987654321", u.getTecnico().getTelefono());
    }

    @Test
    void rechazaUsernameDuplicado() {
        when(repo.existsByUsername("jperez")).thenReturn(true);
        assertThrows(IllegalArgumentException.class, () -> service.crear(formValido(Rol.ROLE_COBRANZA)));
        verify(repo, never()).save(any());
    }

    @Test
    void rechazaPasswordCortaOQueNoCoincide() {
        UsuarioFormDto corta = formValido(Rol.ROLE_COBRANZA);
        corta.setPassword("123");
        corta.setConfirmarPassword("123");
        assertThrows(IllegalArgumentException.class, () -> service.crear(corta));

        UsuarioFormDto distinta = formValido(Rol.ROLE_COBRANZA);
        distinta.setConfirmarPassword("otraClave");
        assertThrows(IllegalArgumentException.class, () -> service.crear(distinta));
    }

    @Test
    void noPermiteDesactivarLaPropiaCuenta() {
        Usuario gerente = new Usuario("gerente", "x", "Gerente", "g@x.com", Rol.ROLE_GERENTE);
        when(repo.findById(1L)).thenReturn(Optional.of(gerente));
        assertThrows(IllegalArgumentException.class, () -> service.cambiarEstado(1L, "gerente"));
        assertTrue(gerente.isActivo());
    }

    @Test
    void desactivaYReactivaOtroUsuario() {
        Usuario u = new Usuario("ana", "x", "Ana", "a@x.com", Rol.ROLE_COBRANZA);
        when(repo.findById(2L)).thenReturn(Optional.of(u));
        service.cambiarEstado(2L, "gerente");
        assertFalse(u.isActivo());
        service.cambiarEstado(2L, "gerente");
        assertTrue(u.isActivo());
    }

    @Test
    void gerenteNoPuedeQuitarseSuPropioRol() {
        Usuario gerente = new Usuario("gerente", "x", "Gerente", "g@x.com", Rol.ROLE_GERENTE);
        gerente.setId(1L);
        when(repo.findById(1L)).thenReturn(Optional.of(gerente));
        UsuarioFormDto dto = formValido(Rol.ROLE_SECRETARIA);
        dto.setId(1L);
        assertThrows(IllegalArgumentException.class, () -> service.actualizar(dto, "gerente"));
    }

    @Test
    void cambiarPasswordExigeLaActualCorrecta() {
        Usuario u = new Usuario("ana", encoder.encode("vieja123"), "Ana", "a@x.com", Rol.ROLE_COBRANZA);
        when(repo.findByUsername("ana")).thenReturn(Optional.of(u));

        assertThrows(IllegalArgumentException.class, () -> service.cambiarPassword("ana", "incorrecta", "nueva123"));

        service.cambiarPassword("ana", "vieja123", "nueva123");
        ArgumentCaptor<Usuario> captor = ArgumentCaptor.forClass(Usuario.class);
        verify(repo).save(captor.capture());
        assertTrue(encoder.matches("nueva123", captor.getValue().getPassword()));
    }
}