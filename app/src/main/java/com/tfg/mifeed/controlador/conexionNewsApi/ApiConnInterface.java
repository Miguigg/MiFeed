package com.tfg.mifeed.controlador.conexionNewsApi;

import com.tfg.mifeed.modelo.RespuestaListaNoticias;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiConnInterface {
    String BASE_URL = "https://newsapi.org/v2/";

    @GET("top-headlines?language=es&sortBy=publishedAt")
    Call<RespuestaListaNoticias> getNoticiasPorTema(
            @Query("pageSize") int pageSize,
            @Query(value = "category",encoded = true) String category ,
            @Query("apiKey") String apiKey
    );

    @GET("everything?language=es&sortBy=publishedAt")
    Call<RespuestaListaNoticias> getNoticiasImportantes(
            @Query("pageSize") int pageSize,
            @Query(value = "domains",encoded = true) String dominios,
            @Query("apiKey") String apiKey
    );
}
