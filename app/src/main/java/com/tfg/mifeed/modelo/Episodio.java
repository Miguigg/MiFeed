package com.tfg.mifeed.modelo;

public class Episodio {
  private String id;
  private String audio;
  private String image;
  private Podcast podcast;
  private String title_original;
  private String tituloPodcast;
  private String description_original;
  private String title;
  private String description;

  public Episodio(
      String id,
      String audio,
      String image,
      Podcast podcast,
      String title,
      String description,
      String title_original,
      String description_original) {
    this.id = id;
    this.audio = audio;
    this.image = image;
    this.podcast = podcast;
    this.title_original = title_original;
    this.description_original = description_original;
    this.title = title;
    this.description = description;
  }

  public Episodio(String nombre,String urlImagen,String urlAudio, String idPodcast, String description, String tituloPodcast){
    this.title = nombre;
    this.image = urlImagen;
    this.audio = urlAudio;
    this.id = idPodcast;
    this.description = description;
    this.tituloPodcast = tituloPodcast;
  }

  public Episodio(String nombre,String urlImagen,String urlAudio){
    this.title = nombre;
    this.image = urlImagen;
    this.audio = urlAudio;
  }

  public String getTituloPodcast() {
    return tituloPodcast;
  }

  public String getTitle() {
    return title;
  }

  public String getDescription() {
    return description;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getAudio() {
    return audio;
  }

  public String getImage() {
    return image;
  }

  public Podcast getPodcast() {
    return podcast;
  }

  public void setPodcast(Podcast podcast) {
    this.podcast = podcast;
  }

  public String getTitle_original() {
    return title_original;
  }

  public String getDescription_original() {
    return description_original;
  }
}
