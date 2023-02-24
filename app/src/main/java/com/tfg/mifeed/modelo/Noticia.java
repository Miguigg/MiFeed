package com.tfg.mifeed.modelo;

public class Noticia {
    private final String author;
    private final String title;
    private final String description;
    private final String url;
    private final String urlToImage;

    public Noticia (String author, String title,String description, String url, String urlToImage){
        this.author = author;
        this.title = title;
        this.description = description;
        this.url = url;
        this.urlToImage = urlToImage;
    }

    public String getAuthor() {
        return author;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getUrl() {
        return url;
    }

    public String getUrlToImage() {
        return urlToImage;
    }

}
