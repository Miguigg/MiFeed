package com.tfg.mifeed.modelo;

public class Usuario {
    private String nombre;
    private String email;
    private String contraseña;

    public Usuario(){

    }

    public Usuario(String nombre, String email, String contraseña) {
        this.nombre = nombre;
        this.email = email;
        this.contraseña = contraseña;
    }

    public String getNombre() {
        return nombre;
    }

    public String getEmail() {
        return email;
    }

    public String getContraseña() {
        return contraseña;
    }
}
