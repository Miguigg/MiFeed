package com.tfg.mifeed.controlador.activities.Activities.GestionCuenta;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.tfg.mifeed.R;
import com.tfg.mifeed.controlador.activities.Activities.BienvenidaActivity;
import com.tfg.mifeed.controlador.activities.Activities.Prensa.FavoritosActivity;
import com.tfg.mifeed.controlador.adaptadores.AdaptadorListaMedios;
import com.tfg.mifeed.controlador.firebase.FirebaseServices;

import java.util.ArrayList;

public class SeleccionMediosActivity extends AppCompatActivity {

    private ConstraintLayout btnAceptar;
    public static ArrayList<String> dominiosSeleccionados = new ArrayList<>();
    public static ArrayList<String> mediosSeleccionados = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seleccion_medios);
        btnAceptar = findViewById(R.id.btnFinalizar);
        FirebaseServices.getMedios(this.findViewById(android.R.id.content));
        btnAceptar.setOnClickListener(v -> {
            insertarMedios();
        });
    }

    private void insertarMedios() {
        if(dominiosSeleccionados.size()<1 || (mediosSeleccionados.size()<1)){
            Toast.makeText(this, R.string.errSesion, Toast.LENGTH_SHORT).show();
        }else{
            FirebaseServices.setMediosUsuario(mediosSeleccionados,dominiosSeleccionados,this.findViewById(android.R.id.content));
            FirebaseServices.setFirstLoginFalse();
        }
    }

    public void insertMedio(String dominio,String nombre){
        dominiosSeleccionados.add(dominio);
        mediosSeleccionados.add(nombre);
    }

    public void deleteMedio(String dominio,String nombre){
        dominiosSeleccionados.remove(dominio);
        mediosSeleccionados.remove(nombre);
    }

    public void setMedios(ArrayList<String> medios, ArrayList<String> dominios, View v){
        AdaptadorListaMedios adaptadorListaMedios1 = new AdaptadorListaMedios(medios,dominios);
        RecyclerView recyclerView1 = v.findViewById(R.id.listaMedios);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(SeleccionMediosActivity.this);
        recyclerView1.setLayoutManager(linearLayoutManager);
        recyclerView1.setAdapter(adaptadorListaMedios1);
    }

    public void respuestaInsercion(String res,View v){
        switch (res){
            case "true":
                v.getContext().startActivity(new Intent(v.getContext(), FavoritosActivity.class));
                finish();
                break;
            case "false":
                FirebaseAuth.getInstance().signOut();
                v.getContext().startActivity(new Intent(v.getContext(), BienvenidaActivity.class));
                finish();
                break;
        }
    }
}