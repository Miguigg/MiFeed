package com.tfg.mifeed.modelo;

import java.util.Date;

public class Recordatorio {
    private Date timestamp;
    private int idRecordatorio;
    private String titulo;
    private String urlAudio;
    private String urlImagen;

    public Recordatorio(Date timestamp, int idRecordatorio, String titulo, String urlAudio, String urlImagen) {
        this.timestamp = timestamp;
        this.idRecordatorio = idRecordatorio;
        this.titulo = titulo;
        this.urlAudio = urlAudio;
        this.urlImagen = urlImagen;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public int getIdRecordatorio() {
        return idRecordatorio;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getUrlAudio() {
        return urlAudio;
    }

    public String getUrlImagen() {
        return urlImagen;
    }

    @Override
    public String toString() {
        return "Recordatorio{" +
                "timestamp=" + timestamp +
                ", idRecordatorio=" + idRecordatorio +
                ", titulo='" + titulo + '\'' +
                ", urlAudio='" + urlAudio + '\'' +
                ", urlImagen='" + urlImagen + '\'' +
                '}';
    }
}
