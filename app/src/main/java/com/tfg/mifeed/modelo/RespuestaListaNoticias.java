package com.tfg.mifeed.modelo;

import java.util.ArrayList;

public class RespuestaListaNoticias {
    private String status;
    private String totalResults;
    private ArrayList<Noticia> articles;

    public RespuestaListaNoticias(String status, String totalResults,ArrayList<Noticia> noticias){
        this.status = status;
        this.totalResults = totalResults;
        this.articles = noticias;
    }


    public ArrayList<Noticia> getNoticias() {
        return articles;
    }

}
