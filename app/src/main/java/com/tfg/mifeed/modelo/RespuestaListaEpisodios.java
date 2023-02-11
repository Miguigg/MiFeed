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

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getNext_offset() {
        return next_offset;
    }

    public void setNext_offset(int next_offset) {
        this.next_offset = next_offset;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public double getTook() {
        return took;
    }

    public void setTook(double took) {
        this.took = took;
    }

    public ArrayList<Episodio> getResults() {
        return results;
    }

    public void setResults(ArrayList<Episodio> results) {
        this.results = results;
    }
}
