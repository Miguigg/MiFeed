package com.tfg.mifeed.controlador.conexioPodcastApi;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiPodcastConn {
    private static Retrofit retrofit = null;

    public static ApiPodcastConnInterface getApiConnInterfacePodcast(){
        if(retrofit==null){
            retrofit = new Retrofit.Builder().baseUrl(ApiPodcastConnInterface.BASE_URL).addConverterFactory(GsonConverterFactory.create()).build();
        }
        return retrofit.create(ApiPodcastConnInterface.class);
    }
}
