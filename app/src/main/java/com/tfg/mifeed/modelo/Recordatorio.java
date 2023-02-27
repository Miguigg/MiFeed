package com.tfg.mifeed.modelo;

import java.util.Date;

public class Recordatorio {
    private final Date timestamp;
    private final int idRecordatorio;
    private final String titulo;
    private final String urlAudio;
    private final String urlImagen;

    private final String idPodcast;

    public Recordatorio(Date timestamp, int idRecordatorio, String titulo, String urlAudio, String urlImagen, String idPodcast) {
        this.timestamp = timestamp;
        this.idRecordatorio = idRecordatorio;
        this.titulo = titulo;
        this.urlAudio = urlAudio;
        this.urlImagen = urlImagen;
        this.idPodcast = idPodcast;
    }

    public String getIdPodcast() {
        return idPodcast;
    }

    public String getTitulo() {
        return titulo;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public int getIdRecordatorio() {
        return idRecordatorio;
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
