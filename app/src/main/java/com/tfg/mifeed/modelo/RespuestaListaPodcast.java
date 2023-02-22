package com.tfg.mifeed.modelo;

import java.util.ArrayList;

public class RespuestaListaPodcast {
    private String id;
    private String title;
    private String image;

    private ArrayList<Episodio> results;

    private ArrayList<Episodio> episodes;

    public String getId() {
        return id;
    }

    public ArrayList<Episodio> getEpisodes() {
        return episodes;
    }

    public void setEpisodes(ArrayList<Episodio> episodes) {
        this.episodes = episodes;
    }

    public ArrayList<Episodio> getResults() {
        return results;
    }

    public void setResults(ArrayList<Episodio> results) {
        this.results = results;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
