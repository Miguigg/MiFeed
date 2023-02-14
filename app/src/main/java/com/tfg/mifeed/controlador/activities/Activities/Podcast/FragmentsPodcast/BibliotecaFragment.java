package com.tfg.mifeed.controlador.activities.Activities.Podcast.FragmentsPodcast;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.tfg.mifeed.R;
import com.tfg.mifeed.controlador.adaptadores.AdaptadorBibliotecaPodcast;
import com.tfg.mifeed.controlador.firebase.FirebaseServices;
import com.tfg.mifeed.modelo.Podcast;

import java.util.ArrayList;
import java.util.Arrays;

public class BibliotecaFragment extends Fragment {

    private static View v;
    private static RecyclerView listaBiblioteca;
    private static TextView err;
    private static ProgressBar carga;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_biblioteca,container,false);
        listaBiblioteca = v.findViewById(R.id.listaBiblioteca);
        err = v.findViewById(R.id.errBiblioteca);
        carga = v.findViewById(R.id.cargaBiblioteca);
        FirebaseServices.getPodcastBiblioteca();
        carga.setVisibility(View.VISIBLE);
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