package com.tfg.mifeed.controlador.utilidades;

import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.webkit.CookieManager;


public class DescargarEpisodio {

    private final String urlEpisodio;
    private final String titulo;
    private final Context context;


    public DescargarEpisodio(String urlEpisodio, String titulo, Context c){
        this.urlEpisodio = urlEpisodio;
        this.titulo = titulo;
        this.context = c;
    }

    public void accionDescarga(){
        Log.d("titulo", titulo);
        String url = this.urlEpisodio;
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        request.setTitle(titulo);
        request.setDescription("Descargando...");
        String cookie = CookieManager.getInstance().getCookie(url);
        request.addRequestHeader("cookie",cookie);
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,titulo);

        DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        downloadManager.enqueue(request);
    }

}
