package com.tfg.mifeed.controlador.activities.Activities.Prensa.FragmentsPrensa;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.tfg.mifeed.R;
import com.tfg.mifeed.controlador.activities.Activities.BienvenidaActivity;
import com.tfg.mifeed.controlador.activities.Activities.GestionCuenta.SeleccionTemasActivity;
import com.tfg.mifeed.controlador.Adaptadores.AdaptadoresPrensa.AdatadorMediosUsuario;
import com.tfg.mifeed.controlador.firebase.FirebaseServices;
import com.tfg.mifeed.controlador.utilidades.CheckConexion;

import java.util.ArrayList;

public class FavoritosFragment extends Fragment {
    public static ArrayList<String> listaMedios = new ArrayList<>();
    public static ArrayList<String> listaTemas = new ArrayList<>();
    public static RecyclerView recyclerViewMedios;
    public static RecyclerView recyclerViewTemas;

    public static ProgressBar cargaFavoritos;

    private ConstraintLayout btnModificar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =inflater.inflate(R.layout.fragment_favoritos, container, false);
        FirebaseServices.getMediosUsuario(view);
        LinearLayoutManager linearLayoutManager1 =
                new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        LinearLayoutManager linearLayoutManager2 =
                new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerViewMedios = view.findViewById(R.id.recyclerViewMedios);
        recyclerViewTemas = view.findViewById(R.id.recyclerViewTemas);
        cargaFavoritos = view.findViewById(R.id.cargaFavoritos);
        recyclerViewMedios.setLayoutManager(linearLayoutManager1);
        recyclerViewTemas.setLayoutManager(linearLayoutManager2);
        btnModificar = view.findViewById(R.id.btnModificar);

        btnModificar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!CheckConexion.getEstadoActual(view.getContext())){
                    Toast.makeText(view.getContext(),R.string.errConn,Toast.LENGTH_LONG).show();
                    cargaFavoritos.setVisibility(View.GONE);
                }else{
                    modificarPreferencias(view);
                }
            }
        });
        return view;
    }

    private void modificarPreferencias(View v) {
        v.getContext().startActivity(new Intent(v.getContext(),SeleccionTemasActivity.class));
        getActivity().onBackPressed();
        getActivity().finish();
    }

    private void inicializarAdaptador(){
        cargaFavoritos.setVisibility(View.GONE);
        AdatadorMediosUsuario adatadorMediosUsuario = new AdatadorMediosUsuario(listaMedios);
        recyclerViewMedios.setAdapter(adatadorMediosUsuario);
        AdatadorMediosUsuario adatadorTemasUsuario = new AdatadorMediosUsuario(listaTemas);
        recyclerViewTemas.setAdapter(adatadorTemasUsuario);

    }
    public void respuestaListaMedios(ArrayList<String> res, String codigo, View v){
        switch (codigo){
            case "true":
                listaMedios = res;
                break;
            case "false":
                Toast.makeText(v.getContext(), R.string.errSesion, Toast.LENGTH_SHORT).show();
                FirebaseAuth.getInstance().signOut();
                v.getContext().startActivity(new Intent(v.getContext(), BienvenidaActivity.class));
                break;
        }
        FirebaseServices.getTemasUsuario(v,"favoritos");
    }

    public void  respuestaListaTemas(ArrayList<String> res, String codigo, View v){
        switch (codigo){
            case "true":
                listaTemas = res;
                break;
            case "false":
                Toast.makeText(v.getContext(), R.string.errSesion, Toast.LENGTH_SHORT).show();
                FirebaseAuth.getInstance().signOut();
                v.getContext().startActivity(new Intent(v.getContext(), BienvenidaActivity.class));
                break;
        }
        this.inicializarAdaptador();
    }
}