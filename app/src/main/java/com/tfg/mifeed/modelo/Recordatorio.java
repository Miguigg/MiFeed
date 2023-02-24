package com.tfg.mifeed.modelo;

import java.util.Date;

public class Recordatorio {
    private final Date timestamp;
    private final int idRecordatorio;
    private final String titulo;
    private final String urlAudio;
    private final String urlImagen;

    public Recordatorio(Date timestamp, int idRecordatorio, String titulo, String urlAudio, String urlImagen) {
        this.timestamp = timestamp;
        this.idRecordatorio = idRecordatorio;
        this.titulo = titulo;
        this.urlAudio = urlAudio;
        this.urlImagen = urlImagen;
    }

    public String getTitulo() {
        return titulo;
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
