package com.tfg.mifeed.modelo;

import java.util.ArrayList;

public class Podcast {
  private String id;
  private String image;
  private ArrayList<Integer> genre_ids;
  private String thumbnail;
  private String listen_score;
  private String title_original;
  private String title_highlighted;
  private String publisher_original;
  private String publisher_highlighted;
  private String listen_score_global_rank;

  public Podcast(
      String id,
      String image,
      ArrayList<Integer> genre_ids,
      String thumbnail,
      String listen_score,
      String title_original,
      String title_highlighted,
      String publisher_original,
      String publisher_highlighted,
      String listen_score_global_rank) {
    this.id = id;
    this.image = image;
    this.genre_ids = genre_ids;
    this.thumbnail = thumbnail;
    this.listen_score = listen_score;
    this.title_original = title_original;
    this.title_highlighted = title_highlighted;
    this.publisher_original = publisher_original;
    this.publisher_highlighted = publisher_highlighted;
    this.listen_score_global_rank = listen_score_global_rank;
  }

  public Podcast(String id, String image, String titulo){
    this.id = id;
    this.image = image;
    this.title_original = titulo;
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

  public void setImage(String image) {
    this.image = image;
  }

  public ArrayList<Integer> getGenre_ids() {
    return genre_ids;
  }

  public void setGenre_ids(ArrayList<Integer> genre_ids) {
    this.genre_ids = genre_ids;
  }

  public String getThumbnail() {
    return thumbnail;
  }

  public void setThumbnail(String thumbnail) {
    this.thumbnail = thumbnail;
  }

  public String getListen_score() {
    return listen_score;
  }

  public void setListen_score(String listen_score) {
    this.listen_score = listen_score;
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

  public String getPublisher_original() {
    return publisher_original;
  }

  public void setPublisher_original(String publisher_original) {
    this.publisher_original = publisher_original;
  }

  public String getPublisher_highlighted() {
    return publisher_highlighted;
  }

  public void setPublisher_highlighted(String publisher_highlighted) {
    this.publisher_highlighted = publisher_highlighted;
  }

  public String getListen_score_global_rank() {
    return listen_score_global_rank;
  }

  public void setListen_score_global_rank(String listen_score_global_rank) {
    this.listen_score_global_rank = listen_score_global_rank;
  }
}
