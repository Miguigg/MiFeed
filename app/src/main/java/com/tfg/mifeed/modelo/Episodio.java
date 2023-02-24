package com.tfg.mifeed.modelo;

public class Episodio {
  private String id;
  private String link;
  private String audio;
  private String image;
  private Podcast podcast;
  private String title_original;
  private String tituloPodcast;
  private String title_highlighted;
  private String description_original;
  private String description_highlighted;
  private String title;
  private String description;

  public Episodio(
      String id,
      String link,
      String audio,
      String image,
      Podcast podcast,
      String title,
      String description,
      String title_original,
      String tituloPodcast,
      String title_highlighted,
      String description_original,
      String description_highlighted) {
    this.id = id;
    this.link = link;
    this.audio = audio;
    this.image = image;
    this.podcast = podcast;
    this.title_original = title_original;
    this.title_highlighted = title_highlighted;
    this.description_original = description_original;
    this.description_highlighted = description_highlighted;
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
