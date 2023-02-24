package com.tfg.mifeed.modelo;

import java.util.ArrayList;

public class RespuestaListaPodcast {
    private String id;


    private ArrayList<Episodio> results;

    private ArrayList<Episodio> episodes;

    public String getId() {
        return id;
    }

    public ArrayList<Episodio> getEpisodes() {
        return episodes;
    }

    public void setId(String id) {
        this.id = id;
    }

}
