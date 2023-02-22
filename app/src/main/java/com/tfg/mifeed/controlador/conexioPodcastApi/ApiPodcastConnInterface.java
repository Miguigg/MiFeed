package com.tfg.mifeed.controlador.conexioPodcastApi;

import com.tfg.mifeed.modelo.RespuestaListaEpisodios;
import com.tfg.mifeed.modelo.RespuestaListaPodcast;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiPodcastConnInterface {
    String BASE_URL = "https://listen-api.listennotes.com/api/v2/";



    @Headers({"X-ListenAPI-Key: c596e1a86e1b4982ace1ab0e2a7c3702"})
    @GET("podcasts/{id}")
    Call<RespuestaListaPodcast> getEpisodiosConid(
            @Path("id") String id,
            @Query("sort") String parametro
    );

    @Headers({"X-ListenAPI-Key: c596e1a86e1b4982ace1ab0e2a7c3702"})
    @GET("search")
    Call<RespuestaListaEpisodios> getEpisodiosConBusqueda(
            @Query("q") String nombre,
            @Query("sort_by_date") int valor,
            @Query("type") String episode,
            @Query("offset") String offset,
            @Query("language") String lang
    );
}
