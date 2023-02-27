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
import com.tfg.mifeed.controlador.Adaptadores.AdaptadoresPodcast.AdaptadorBibliotecaPodcast;
import com.tfg.mifeed.controlador.firebase.FirebaseServices;
import com.tfg.mifeed.controlador.utilidades.CheckConexion;
import com.tfg.mifeed.modelo.Podcast;

import java.util.ArrayList;

public class BibliotecaFragment extends Fragment {

    private static View v;
    private static RecyclerView listaBiblioteca;
    private static TextView err;
    private static ProgressBar carga;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_biblioteca,container,false);
        listaBiblioteca = v.findViewById(R.id.listaBiblioteca);
        err = v.findViewById(R.id.errBiblioteca);
        carga = v.findViewById(R.id.cargaBiblioteca);
        carga.setVisibility(View.VISIBLE);
        if(!CheckConexion.getEstadoActual(v.getContext())) {
            Toast.makeText(v.getContext(), R.string.errConn, Toast.LENGTH_LONG).show();
            carga.setVisibility(View.GONE);
        }else{
            FirebaseServices.getPodcastBiblioteca();
        }
        return v;
    }

    public void respuestaBiblioteca(String res, ArrayList<Podcast> listaPodcast){
        switch (res){
            case "true":
                LinearLayoutManager linearLayoutManager =
                        new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
                listaBiblioteca.setLayoutManager(linearLayoutManager);
                AdaptadorBibliotecaPodcast adaptadorBibliotecaPodcast = new AdaptadorBibliotecaPodcast(listaPodcast,v.getContext());
                listaBiblioteca.setAdapter(adaptadorBibliotecaPodcast);
                err.setVisibility(View.GONE);
                carga.setVisibility(View.GONE);
                break;
            case "false":
                err.setVisibility(View.VISIBLE);
                carga.setVisibility(View.GONE);
                break;
        }
    }
}