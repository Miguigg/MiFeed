package com.tfg.mifeed.controlador.activities.Activities.Prensa.FragmentsPrensa;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
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
import com.tfg.mifeed.controlador.adaptadores.AdaptadorListaNoticias;
import com.tfg.mifeed.controlador.conexionNewsApi.ApiConn;
import com.tfg.mifeed.controlador.firebase.FirebaseServices;
import com.tfg.mifeed.modelo.Noticia;
import com.tfg.mifeed.modelo.RespuestaListaNoticias;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ImportantesFragment extends Fragment {
    private final String API_KEY = "c3afd5ea3f5548adbe7afc7e21c4c0bf";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =inflater.inflate(R.layout.fragment_importantes, container, false);
        FirebaseServices.getDominios(view);
        return view;
    }

    public void respuestaListaDominios(ArrayList<String> listaDominios,String res, View v){
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
        String listString = String.join(", ", listaDominios);
        ArrayList<Noticia> respuesta = new ArrayList<>();
        RecyclerView listaNoticias = v.findViewById(R.id.listaNotiicasImportantes);
        LinearLayoutManager linearLayoutManager =
                new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        AdaptadorListaNoticias adaptadorListaNoticias = new AdaptadorListaNoticias(v.getContext(),respuesta,v);
        listaNoticias.setLayoutManager(linearLayoutManager);
        listaNoticias.setAdapter(adaptadorListaNoticias);
        TextView err = v.findViewById(R.id.errNoticias);
        ProgressBar carga = v.findViewById(R.id.cargaImportantes);
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
        Log.d("lista",listString);
    }
}