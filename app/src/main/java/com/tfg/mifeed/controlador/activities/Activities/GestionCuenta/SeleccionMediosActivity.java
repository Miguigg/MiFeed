package com.tfg.mifeed.controlador.activities.Activities.GestionCuenta;

import android.app.Activity;
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
import com.tfg.mifeed.controlador.activities.Activities.Prensa.PrensaActivity;
import com.tfg.mifeed.controlador.Adaptadores.AdaptadoresPrensa.AdaptadorListaMedios;
import com.tfg.mifeed.controlador.firebase.FirebaseGestionUsuario;
import com.tfg.mifeed.controlador.firebase.FirebaseServices;
import com.tfg.mifeed.controlador.utilidades.CheckConexion;
import com.tfg.mifeed.modelo.MediosModel;

import java.util.ArrayList;

public class SeleccionMediosActivity extends AppCompatActivity {

    private ConstraintLayout btnAceptar;
    public static Activity activity;
    public static ArrayList<String>  dominiosSeleccionados = new ArrayList<>();
    public static ArrayList<String> mediosSeleccionados = new ArrayList<>();
    public static View v;
    public FirebaseGestionUsuario firebaseGestionUsuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seleccion_medios);
        btnAceptar = findViewById(R.id.btnFinalizar);
        v = this.findViewById(android.R.id.content);
        dominiosSeleccionados = new ArrayList<>();
        mediosSeleccionados = new ArrayList<>();
        activity = (Activity) v.getContext();
        firebaseGestionUsuario = new FirebaseGestionUsuario();
        FirebaseServices.getMedios();

        btnAceptar.setOnClickListener(v -> {
            if(!CheckConexion.getEstadoActual(SeleccionMediosActivity.this)){
                Toast.makeText(SeleccionMediosActivity.this,R.string.errConn,Toast.LENGTH_LONG).show();
            }else{
                insertarMedios();
            }
        });
    }

    private void insertarMedios() {
        /*
        * Una vez seleccionados los medios se mandan a firebase
        * */
        if(dominiosSeleccionados.size()<1 || (mediosSeleccionados.size()<1)){
            Toast.makeText(this, R.string.errSeleccionTemas, Toast.LENGTH_SHORT).show();
        }else{
            FirebaseGestionUsuario.setMediosUsuario(mediosSeleccionados,dominiosSeleccionados);
            FirebaseGestionUsuario.setFirstLoginFalse();
        }
    }

    public void insertarDominio(String dominio, String nombre){
        /*
        * Funcion llamada desde el adaptador y que inserta en la lista de seleccionados aquellos medios que se
        * han pulsado
        * */
        dominiosSeleccionados.add(dominio);
        mediosSeleccionados.add(nombre);
    }

    public void deleteDominio(String dominio, String nombre){
        /*
         * Funcion llamada desde el adaptador y que elimina en la lista de seleccionados aquellos medios que se
         * han pulsado
         * */
        dominiosSeleccionados.remove(dominio);
        mediosSeleccionados.remove(nombre);
    }

    public void setMedios(ArrayList<MediosModel> medios, String res){
        /*
        * Inicializa el adaptador y le mete los medios que estan disponibles en Firebase
        * Si no se pudieron obtener los medios, se muestra un error
        * */
        switch (res){
            case "true":
                AdaptadorListaMedios adaptadorListaMedios1 = new AdaptadorListaMedios(medios,SeleccionMediosActivity.this);
                RecyclerView recyclerView1 = v.findViewById(R.id.listaMedios);
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(SeleccionMediosActivity.this);
                recyclerView1.setLayoutManager(linearLayoutManager);
                recyclerView1.setAdapter(adaptadorListaMedios1);
                break;
            case "false":
                Toast.makeText(v.getContext(), R.string.errConn, Toast.LENGTH_LONG).show();
                break;
        }
    }

    public void respuestaInsercion(String res){
        /*
        * Comunica al usuario si se ha realizado la insercion correctamente
        * */
        switch (res){
            case "true":
                Intent intent = new Intent(v.getContext(), PrensaActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                v.getContext().startActivity(intent);
                break;
            case "false":
                Toast.makeText(v.getContext(), R.string.errSesion, Toast.LENGTH_SHORT).show();
                FirebaseAuth.getInstance().signOut();
                v.getContext().startActivity(new Intent(v.getContext(), BienvenidaActivity.class));
                finish();
                break;
        }
    }
    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        finish();
    }
}