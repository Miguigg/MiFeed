package com.tfg.mifeed.modelo;

import java.util.ArrayList;

public class RespuestaListaNoticias {

    private final ArrayList<Noticia> articles;

    public RespuestaListaNoticias(ArrayList<Noticia> noticias){
        this.articles = noticias;
    }


    public ArrayList<Noticia> getNoticias() {
        return articles;
    }

}
