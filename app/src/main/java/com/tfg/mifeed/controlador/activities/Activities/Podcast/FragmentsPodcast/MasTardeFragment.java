package com.tfg.mifeed.controlador.activities.Activities.Podcast.FragmentsPodcast;

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
import com.tfg.mifeed.controlador.Adaptadores.AdaptadoresPodcast.AdaptadorListaMasTarde;
import com.tfg.mifeed.controlador.firebase.FirebaseServices;
import com.tfg.mifeed.controlador.utilidades.CheckConexion;
import com.tfg.mifeed.modelo.Episodio;

import java.util.ArrayList;

public class MasTardeFragment extends Fragment {

    private static View v;
    private static ProgressBar carga;
    private static TextView err;
    private static RecyclerView listaEpisodiosRecycleview;
    private static AdaptadorListaMasTarde adaptadorListaMasTarde;
    private static ArrayList<Episodio> listaEpisodios;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        v = inflater.inflate(R.layout.fragment_mas_tarde,container,false);
        listaEpisodios = new ArrayList<>();
        carga = v.findViewById(R.id.cargaMasTarde);
        carga.setVisibility(View.VISIBLE);
        err = v.findViewById(R.id.errMasTarde);
        listaEpisodiosRecycleview = v.findViewById(R.id.listaMastarde);
        if(!CheckConexion.getEstadoActual(v.getContext())){
            Toast.makeText(v.getContext(),R.string.errConn,Toast.LENGTH_LONG).show();
            carga.setVisibility(View.GONE);
        }else{
            FirebaseServices.getPodcastMasTarde(v);
        }
        return v;
    }

    private void setAdaptadorListaMasTarde(){
        LinearLayoutManager linearLayoutManager =
                new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        AdaptadorListaMasTarde adaptadorListaMasTarde1 = new AdaptadorListaMasTarde(listaEpisodios,v.getContext());
        listaEpisodiosRecycleview.setAdapter(adaptadorListaMasTarde1);
        listaEpisodiosRecycleview.setLayoutManager(linearLayoutManager);
    }

    public void respuestaListaPodcast(ArrayList<Episodio> listaEpisodiosNueva, String res, View v){
        switch (res){
            case "true":
                listaEpisodios = listaEpisodiosNueva;
                carga.setVisibility(View.GONE);
                err.setVisibility(View.GONE);
                setAdaptadorListaMasTarde();
                break;
            case "false":
                carga.setVisibility(View.GONE);
                err.setVisibility(View.VISIBLE);
                break;
        }
    }
}