package com.tfg.mifeed.controlador.conexionNewsApi;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiConn {
    private static Retrofit retrofit = null;

    public static ApiConnInterface getApiConnInterface(){
        if(retrofit==null){
            retrofit = new Retrofit.Builder().baseUrl(ApiConnInterface.BASE_URL).addConverterFactory(GsonConverterFactory.create()).build();
        }
        return retrofit.create(ApiConnInterface.class);
    }
}
