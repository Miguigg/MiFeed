package com.tfg.mifeed.controlador.activities.Activities.Prensa.FragmentsPrensa;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.tfg.mifeed.R;
import com.tfg.mifeed.controlador.Adaptadores.AdaptadoresPrensa.AdaptadorListaNoticias;
import com.tfg.mifeed.controlador.conexionNewsApi.ApiConn;
import com.tfg.mifeed.controlador.firebase.FirebaseServices;
import com.tfg.mifeed.controlador.utilidades.CheckConexion;
import com.tfg.mifeed.modelo.Noticia;
import com.tfg.mifeed.modelo.RespuestaListaNoticias;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ImportantesFragment extends Fragment {
    private final String API_KEY = "c3afd5ea3f5548adbe7afc7e21c4c0bf";
    private static ProgressBar carga;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =inflater.inflate(R.layout.fragment_importantes, container, false);
        carga = view.findViewById(R.id.cargaImportantes);
        if(!CheckConexion.getEstadoActual(view.getContext())){
            Toast.makeText(view.getContext(),R.string.errConn,Toast.LENGTH_LONG).show();
            carga.setVisibility(View.GONE);
        }else{
            FirebaseServices.getDominios(view);
        }
        return view;
    }

    public void respuestaListaDominios(ArrayList<String> listaDominios,String res, View v){
        /*
        *recibe la lista de dominios que el usuario tiene en favoritos y, en fucion de si la ha obtenido
        * de forma exitosa, genera la lista de noticias
        * */
        switch (res){
            case "true":
                generarLista(listaDominios,v);
                break;
            case "false":
                Toast.makeText(v.getContext(), R.string.errConn, Toast.LENGTH_SHORT).show();
                break;
        }
    }

    private void generarLista(ArrayList<String> listaDominios,View v) {
        /*
        * Con la lista de dominios manda una peticion a la API para obtener la lista
        * */
        String listString = String.join(", ", listaDominios);
        ArrayList<Noticia> respuesta = new ArrayList<>();
        RecyclerView listaNoticias = v.findViewById(R.id.listaNotiicasImportantes);
        LinearLayoutManager linearLayoutManager =
                new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        AdaptadorListaNoticias adaptadorListaNoticias = new AdaptadorListaNoticias(v.getContext(),respuesta,v);
        listaNoticias.setLayoutManager(linearLayoutManager);
        listaNoticias.setAdapter(adaptadorListaNoticias);
        TextView err = v.findViewById(R.id.errNoticias);
        carga = v.findViewById(R.id.cargaImportantes);
        ApiConn.getApiConnInterface().getNoticiasImportantes(100,listString,API_KEY).enqueue(new Callback<RespuestaListaNoticias>() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onResponse(Call<RespuestaListaNoticias> call, Response<RespuestaListaNoticias> response) {
                if(response.body() != null){
                    respuesta.clear();
                    respuesta.addAll(response.body().getNoticias());
                    adaptadorListaNoticias.notifyDataSetChanged();
                    err.setVisibility(View.GONE);
                    carga.setVisibility(View.GONE);
                }else{
                    err.setVisibility(View.VISIBLE);
                    carga.setVisibility(View.GONE);
                }
            }
            @Override
            public void onFailure(Call<RespuestaListaNoticias> call, Throwable t) {
                err.setVisibility(View.VISIBLE);
                carga.setVisibility(View.GONE);
            }
        });
    }
}