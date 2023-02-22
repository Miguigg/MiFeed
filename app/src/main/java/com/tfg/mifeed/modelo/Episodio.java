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

  public String getTituloPodcast() {
    return tituloPodcast;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getLink() {
    return link;
  }

  public void setLink(String link) {
    this.link = link;
  }

  public String getAudio() {
    return audio;
  }

  public void setAudio(String audio) {
    this.audio = audio;
  }

  public String getImage() {
    return image;
  }

  public void setImage(String image) {
    this.image = image;
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

  public void setTitle_original(String title_original) {
    this.title_original = title_original;
  }

  public String getTitle_highlighted() {
    return title_highlighted;
  }

  public void setTitle_highlighted(String title_highlighted) {
    this.title_highlighted = title_highlighted;
  }

  public String getDescription_original() {
    return description_original;
  }

  public void setDescription_original(String description_original) {
    this.description_original = description_original;
  }

  public String getDescription_highlighted() {
    return description_highlighted;
  }

  public void setDescription_highlighted(String description_highlighted) {
    this.description_highlighted = description_highlighted;
  }
}
