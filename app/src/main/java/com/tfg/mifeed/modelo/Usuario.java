package com.tfg.mifeed.modelo;

public class Usuario {
  private String nombre;
  private String email;
  private String contraseña;
  private boolean notificaciones;
  private boolean etiquetasNube;

  public Usuario(String nombre, String email, String contraseña) {
    this.nombre = nombre;
    this.email = email;
    this.contraseña = contraseña;
  }

  public Usuario(String nombre, String email, String contraseña,boolean notificaciones,boolean etiquetasNube) {
    this.nombre = nombre;
    this.email = email;
    this.contraseña = contraseña;
    this.notificaciones = notificaciones;
    this.etiquetasNube = etiquetasNube;
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

  public boolean isNotificaciones() {
    return notificaciones;
  }

  public boolean isEtiquetasNube() {
    return etiquetasNube;
  }

  public void setContraseña(String contraseña) {
    this.contraseña = contraseña;
  }
}
