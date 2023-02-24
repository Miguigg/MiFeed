package com.tfg.mifeed.modelo;

import java.util.ArrayList;

public class Podcast {
  private String id;
  private final String image;
  private final String title_original;
  private String descripcion;

  public Podcast(
      String id,
      String image,
      String title_original) {
    this.id = id;
    this.image = image;
    this.title_original = title_original;
  }

  public Podcast(String id, String image, String titulo, String descipcion){
    this.id = id;
    this.image = image;
    this.title_original = titulo;
    this.descripcion = descipcion;
  }

  public String getDescripcion() {
    return descripcion;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getImage() {
    return image;
  }

  public String getTitle_original() {
    return title_original;
  }
}
