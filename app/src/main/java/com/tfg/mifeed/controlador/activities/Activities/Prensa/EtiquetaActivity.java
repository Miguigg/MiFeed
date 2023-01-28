package com.tfg.mifeed.controlador.activities.Activities.Prensa;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.tfg.mifeed.R;
import com.tfg.mifeed.controlador.adaptadores.AdaptadorListaArticulosEtiqueta;

import java.util.ArrayList;

public class EtiquetaActivity extends AppCompatActivity {
    private RecyclerView listaEtiquetas;
    private ProgressBar carga;
    private TextView err,txtTituloArticulos;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_etiqueta);
        err = findViewById(R.id.errArticulos);
        carga = findViewById(R.id.cargaArticulos);
        listaEtiquetas = findViewById(R.id.listaArticulos);
        txtTituloArticulos = findViewById(R.id.txtTituloArticulos);

        ArrayList<String> urls = (ArrayList<String>) getIntent().getStringArrayListExtra("urls");
        ArrayList<String> titulosPagina = (ArrayList<String>) getIntent().getStringArrayListExtra("titulosPagina");
        String tituloEtiqueta = getIntent().getExtras().getString("Nombre");

        txtTituloArticulos.setText(tituloEtiqueta);
        if(urls.size()<1 || titulosPagina.size()<1){
            err.setVisibility(View.VISIBLE);
            carga.setVisibility(View.GONE);
        }else{
            LinearLayoutManager linearLayoutManager =
                    new LinearLayoutManager(this.findViewById(android.R.id.content).getContext(), LinearLayoutManager.VERTICAL, false);
            AdaptadorListaArticulosEtiqueta adaptadorListaArticulosEtiqueta = new AdaptadorListaArticulosEtiqueta(urls,titulosPagina,tituloEtiqueta,this.findViewById(android.R.id.content));

            listaEtiquetas.setLayoutManager(linearLayoutManager);
            listaEtiquetas.setAdapter(adaptadorListaArticulosEtiqueta);
            err.setVisibility(View.GONE);
            carga.setVisibility(View.GONE);
        }
    }
}