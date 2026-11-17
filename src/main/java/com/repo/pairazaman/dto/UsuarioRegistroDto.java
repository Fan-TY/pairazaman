package com.repo.pairazaman.dto;

import com.repo.pairazaman.model.Rol;

/**
 * DTO para el registro de nuevos usuarios en el sistema.
 */
public class UsuarioRegistroDto {

    private String username;
    private String password;
    private String confirmarPassword;
    private String nombreCompleto;
    private String email;
    private Rol rol;

    public UsuarioRegistroDto() {
        this.rol = Rol.ROLE_OPERADOR;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getConfirmarPassword() {
        return confirmarPassword;
    }

    public void setConfirmarPassword(String confirmarPassword) {
        this.confirmarPassword = confirmarPassword;
    }

    public String getNombreCompleto() {
        return nombreCompleto;
    }

    public void setNombreCompleto(String nombreCompleto) {
        this.nombreCompleto = nombreCompleto;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Rol getRol() {
        return rol;
    }

    public void setRol(Rol rol) {
        this.rol = rol;
    }
}
