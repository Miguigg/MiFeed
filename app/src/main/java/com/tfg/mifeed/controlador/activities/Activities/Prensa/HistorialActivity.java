package com.tfg.mifeed.controlador.activities.Activities.Prensa;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.tfg.mifeed.R;
import com.tfg.mifeed.controlador.Adaptadores.AdaptadoresPrensa.AdaptadorHistorial;
import com.tfg.mifeed.controlador.firebase.FirebaseNoticias;
import com.tfg.mifeed.controlador.utilidades.CheckConexion;

import java.util.ArrayList;

public class HistorialActivity extends AppCompatActivity {
    public static RecyclerView listaHistorial;
    public static ProgressBar carga;
    public static TextView err;
    public static AdaptadorHistorial adaptadorHistorial;
    public static ArrayList<String> historialUsuario;
    public static View v;
    private ConstraintLayout btnEliminarHistorial;
    public FirebaseNoticias firebaseNoticias;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historial);
        firebaseNoticias = new FirebaseNoticias();
        v = this.findViewById(android.R.id.content);
        listaHistorial = findViewById(R.id.listaHistorial);
        carga = findViewById(R.id.cargaHistorial);
        err = findViewById(R.id.errHistorial);
        FirebaseNoticias.getHistorial();
        LinearLayoutManager linearLayoutManager =
                new LinearLayoutManager(v.getContext(), LinearLayoutManager.VERTICAL, false);
        listaHistorial.setLayoutManager(linearLayoutManager);
        historialUsuario = new ArrayList<>();
        btnEliminarHistorial = findViewById(R.id.btnEliminarHistorial);
        if(!CheckConexion.getEstadoActual(HistorialActivity.this)){
            Toast.makeText(HistorialActivity.this,R.string.errConn,Toast.LENGTH_LONG).show();
        }else{
            btnEliminarHistorial.setOnClickListener(v -> {
                eliminarTodoHistorial();
            });
        }
    }

    private void eliminarTodoHistorial() {
        FirebaseNoticias.eliminarTodoHistorial(v);
        FirebaseNoticias.getHistorial();
    }

    public void respuestaHistorial(ArrayList<String> historial){
        adaptadorHistorial = new AdaptadorHistorial(historial,v.getContext());
        listaHistorial.setAdapter(adaptadorHistorial);
        carga.setVisibility(View.GONE);
        if(historial.size()<1){
            err.setVisibility(View.VISIBLE);
        }
    }
}