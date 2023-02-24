package com.tfg.mifeed.modelo;

import java.util.ArrayList;

public class RespuestaListaEpisodios {
    private int count;
    private int next_offset;
    private int total;
    private double took;
    private ArrayList<Episodio> results;

    public RespuestaListaEpisodios(int count, int next_offset, int total, double took, ArrayList<Episodio> results) {
        this.count = count;
        this.next_offset = next_offset;
        this.total = total;
        this.took = took;
        this.results = results;
    }



    public ArrayList<Episodio> getResults() {
        return results;
    }
}
