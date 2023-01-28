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
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTotalResults() {
        return totalResults;
    }

    public void setTotalResults(String totalResults) {
        this.totalResults = totalResults;
    }

    public ArrayList<Noticia> getNoticias() {
        return articles;
    }

    public void setNoticias(ArrayList<Noticia> noticias) {
        this.articles = noticias;
    }
}
