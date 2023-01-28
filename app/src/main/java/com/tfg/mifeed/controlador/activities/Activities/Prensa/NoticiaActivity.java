package com.tfg.mifeed.controlador.activities.Activities.Prensa;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

import com.tfg.mifeed.R;
import com.tfg.mifeed.controlador.adaptadores.AdaptadorListaNombresEtiquetas;
import com.tfg.mifeed.controlador.firebase.FirebaseServices;

import java.util.ArrayList;

public class NoticiaActivity extends AppCompatActivity {
    private WebView noticia;

    public static ArrayList<String> listaNombres;
    public static String nombreWeb,nombreEtiqueta,url;

    private ImageView btnAtrasWebview,btnAddTag,btnCompartir;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView( R.layout.activity_noticia);
        noticia = findViewById(R.id.webNoticia);
        btnAddTag = findViewById(R.id.btnAddTag);
        btnCompartir = findViewById(R.id.btnCompartir);
        btnAtrasWebview = findViewById(R.id.btnAtrasWebview);
        url = getIntent().getExtras().getString("enlace");

        btnAtrasWebview.setOnClickListener(v -> {
            startActivity(new Intent(NoticiaActivity.this,PrensaActivity.class));
            finish();
        });

        btnAddTag.setOnClickListener(v -> {
            FirebaseServices.getNombresEtiquetas(this.findViewById(android.R.id.content),getLayoutInflater());
        });

        btnCompartir.setOnClickListener(v->{
            //todo mirar como abrir el menu compartir en android
        });
    }

    public void respuestaNombresEtiquetas(String res, ArrayList<String> nombres, View v, LayoutInflater inf){
        switch (res){
            case "true":
                listaNombres = nombres;
                mostrarDialogo(v,inf);
                break;
            case "false":
                break;
        }
    }

    private void mostrarDialogo(View v,LayoutInflater inf){
        AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
        builder.setTitle(R.string.txtTituloEtiqueta);
        View dialog_layout = inf.inflate(R.layout.layout_dialogo, null);
        EditText inputNombreWeb = dialog_layout.findViewById(R.id.txtNombreWeb);
        Spinner lista=  dialog_layout.findViewById(R.id.spinnerNombresEtiquetas);

        AdaptadorListaNombresEtiquetas adaptador = new AdaptadorListaNombresEtiquetas(listaNombres);
        lista.setAdapter(adaptador);

        builder.setView(dialog_layout);

        builder.setPositiveButton(
                R.string.txtAceptar,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        nombreWeb = inputNombreWeb.getText().toString();
                        nombreEtiqueta = lista.getSelectedItem().toString();
                        FirebaseServices.insertarUrlEtiqueta(url,nombreWeb,nombreEtiqueta);
                    }
                });
        builder.setNegativeButton(
                R.string.atras,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        builder.show();
    }
}