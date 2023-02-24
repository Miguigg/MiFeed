package com.tfg.mifeed.modelo;

import java.util.ArrayList;

public class RespuestaListaEpisodios {

    private final ArrayList<Episodio> results;

    public RespuestaListaEpisodios(ArrayList<Episodio> results) {
        this.results = results;
    }

    public ArrayList<Episodio> getResults() {
        return results;
    }
}
