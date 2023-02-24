package com.tfg.mifeed.modelo;

import java.util.ArrayList;

public class Etiqueta {
    private final String tituloEtiqueta;
    private final ArrayList<String> urls;
    private final ArrayList<String> titulos;

    public Etiqueta(String tituloEtiqueta, ArrayList<String> urls,ArrayList<String> titulos) {
        this.tituloEtiqueta = tituloEtiqueta;
        this.urls = urls;
        this.titulos = titulos;
    }

    public String getTituloEtiqueta() {
        return tituloEtiqueta;
    }

    public ArrayList<String> getUrls() {
        return urls;
    }

    public ArrayList<String> getTitulos() {
        return titulos;
    }

}
