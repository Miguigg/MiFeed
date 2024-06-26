package com.tfg.mifeed.controlador.activities.Activities.Prensa;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.tfg.mifeed.R;
import com.tfg.mifeed.controlador.Adaptadores.AdaptadoresPrensa.AdaptadorListaNombresEtiquetas;
import com.tfg.mifeed.controlador.firebase.FirebaseNoticias;
import com.tfg.mifeed.controlador.utilidades.CheckConexion;

import java.util.ArrayList;

public class NoticiaActivity extends AppCompatActivity {
    private WebView noticia;
    private static View v;
    public static ArrayList<String> listaNombres;
    public static String nombreWeb,nombreEtiqueta,url;
    public static TextView errWeb;
    private ImageView btnAtrasWebview,btnAddTag,btnCompartir;
    public FirebaseNoticias firebaseNoticias;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_noticia);
        noticia = findViewById(R.id.webNoticia);
        btnAddTag = findViewById(R.id.btnAddTag);
        btnCompartir = findViewById(R.id.btnCompartir);
        btnAtrasWebview = findViewById(R.id.btnAtrasWebview);
        url = getIntent().getExtras().getString("enlace");
        errWeb = findViewById(R.id.errWeb);
        v = this.findViewById(android.R.id.content);
        firebaseNoticias = new FirebaseNoticias();
        if(url.substring(0,5).equals("http:")){
            url = "https:"+url.substring(5);
        }
        accederNoticia(url);
        if(!CheckConexion.getEstadoActual(NoticiaActivity.this)){
            Toast.makeText(NoticiaActivity.this,R.string.errConn,Toast.LENGTH_LONG).show();
        }else{
            btnAtrasWebview.setOnClickListener(v -> {
                startActivity(new Intent(NoticiaActivity.this,PrensaActivity.class));
                finish();
            });

            btnAddTag.setOnClickListener(v -> {
                FirebaseNoticias.getNombresEtiquetas(getLayoutInflater());
            });

            btnCompartir.setOnClickListener(v->{
                compartirNoticia();
            });
            FirebaseNoticias.insertarHistorial(url);
        }
    }

    public void respuestaNombresEtiquetas(String res, ArrayList<String> nombres, LayoutInflater inf){
        /*
        * En caso de haber encontrado etiquetas se reciben para mostrarlas en el menu selector de
        * etiqueta.
        * */
        switch (res){
            case "true":
                listaNombres = nombres;
                mostrarDialogo(inf);
                break;
            case "false":
                Toast.makeText(v.getContext(), "No tienes etiquetas", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    private void mostrarDialogo(LayoutInflater inf){
        /*
        * Lanza el menu contextual en el que el usuario introduce el nombre que le quiere dar al articulo
        * y la etiqueta donde lo quiere meter
        * */
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
                        if(!inputNombreWeb.getText().toString().isEmpty()){
                            FirebaseNoticias.insertarUrlEtiqueta(url,nombreWeb,nombreEtiqueta);
                            errWeb.setVisibility(View.GONE);

                        }else{
                            errWeb.setVisibility(View.VISIBLE);
                        }
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

    private void accederNoticia(String url){
        /*
        * Con la url recibida accede a la web
        * */
        WebSettings ws = noticia.getSettings();
        ws.setJavaScriptEnabled(true);
        noticia.setWebViewClient(new WebViewClient());
        noticia.loadUrl(url);
    }

    private void compartirNoticia(){
        /*
        * Con el boton de compartir permite mandar la url de la web visitada por distintas aplicaciones
        * */
      Intent intent = new Intent();
      intent.setAction(Intent.ACTION_SEND);
      intent.putExtra(Intent.EXTRA_TEXT,url);
      intent.setType("text/plain");
      startActivity(intent);
    }
}