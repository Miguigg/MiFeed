package com.tfg.mifeed.controlador.activities.Activities.Prensa.FragmentsPrensa;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.tfg.mifeed.R;
import com.tfg.mifeed.controlador.activities.Activities.Prensa.EtiquetaActivity;
import com.tfg.mifeed.controlador.Adaptadores.AdaptadoresPrensa.AdaptadorListaEtiquetas;
import com.tfg.mifeed.controlador.activities.Activities.Prensa.PrensaActivity;
import com.tfg.mifeed.controlador.firebase.FirebaseGestionUsuario;
import com.tfg.mifeed.controlador.firebase.FirebaseNoticias;
import com.tfg.mifeed.controlador.firebase.FirebaseServices;
import com.tfg.mifeed.controlador.utilidades.CheckConexion;
import com.tfg.mifeed.controlador.utilidades.Validaciones;
import com.tfg.mifeed.modelo.Etiqueta;

import java.util.ArrayList;

public class EtiquetasFragment extends Fragment {

  private ImageView addEtiqueta;
  private String tituloNuevaEtiqueta;

  private static RecyclerView listaEtiquetas;
  private static TextView err;
  private static ProgressBar carga;

  public FirebaseNoticias firebaseGestionUsuario;

  private View v;

  @Override
  public View onCreateView(
      LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    v = inflater.inflate(R.layout.fragment_etiquetas, container, false);
    addEtiqueta = v.findViewById(R.id.addEtiqueta);
    err = v.findViewById(R.id.errEtiquetas);
    listaEtiquetas = v.findViewById(R.id.listaEtiquetas);
    carga = v.findViewById(R.id.cargaEtiquetas);
    firebaseGestionUsuario = new FirebaseNoticias();

    if(!CheckConexion.getEstadoActual(v.getContext())){
      Toast.makeText(v.getContext(),R.string.errConn,Toast.LENGTH_LONG).show();
      carga.setVisibility(View.GONE);
    }else{
      addEtiqueta.setOnClickListener(
              v -> {
                inputTitulo(v);
              });
      FirebaseNoticias.getEtiquetas(v);
    }
    return v;
  }

  private void inputTitulo(View v) {
    /*
    * Despliega el menu contextual para introducir el nombre que se le quiere asignar a la etiqueta
    * */
    AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
    builder.setTitle(R.string.txtTituloEtiqueta);
    final EditText input = new EditText(v.getContext());
    input.setInputType(InputType.TYPE_CLASS_TEXT);
    builder.setView(input);

    builder.setPositiveButton(
        R.string.txtAceptar,
        new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int which) {
            tituloNuevaEtiqueta = input.getText().toString();
            addEtiqueta(tituloNuevaEtiqueta, v);
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

  private void addEtiqueta(String titulo, View v) {
    /*
    * Verifica que el titulo no este vacio y crea la etiqueta en firebase
    * */
    if(titulo.isEmpty()){
      Toast.makeText(v.getContext(),R.string.errTituloEtiquetaVacia,Toast.LENGTH_LONG).show();
    }else{
      FirebaseNoticias.crearEtiqueta(titulo,v);
    }
  }
  @SuppressLint("NotifyDataSetChanged")
  public void respuestaGetEtiquetas(String res, ArrayList<Etiqueta> lista, View view) {
    /*
    * Recibe la lista de etiquetas que hay en firebase. En caso de no haber etiquetas comunica que no
    * se han entcontrado
    * */
    switch (res) {
      case "true":
        LinearLayoutManager linearLayoutManager =
                new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        listaEtiquetas.setLayoutManager(linearLayoutManager);
        AdaptadorListaEtiquetas adaptadorListaEtiquetas = new AdaptadorListaEtiquetas(lista,view);
        listaEtiquetas.setAdapter(adaptadorListaEtiquetas);
        adaptadorListaEtiquetas.notifyDataSetChanged();
        err.setVisibility(View.GONE);
        carga.setVisibility(View.GONE);
        break;
      case "false":
        err.setVisibility(View.VISIBLE);
        carga.setVisibility(View.GONE);
        break;
    }
  }

  public void respuestaCreacionEtiqueta(String res, View view) {
    /*
    * Comunica al usuario el resultado del proceso de creacion de la etiqueta
    * */
    switch (res) {
      case "true":
        FirebaseNoticias.getEtiquetas(view);
        break;
      case "false":
        Toast.makeText(v.getContext(), R.string.errConn, Toast.LENGTH_SHORT).show();
        break;
    }
  }

  public void abrirEtiqueta(Etiqueta e,View view){
    /*
    * Si el usuario pulsa alguna etqueta se redirige al usuario a la pestaña con los artuculos dentro
    * */
    Intent intent = new Intent(view.getContext(), EtiquetaActivity.class);
    intent.putExtra("Nombre",e.getTituloEtiqueta());
    intent.putExtra("urls",e.getUrls());
    intent.putExtra("titulosPagina",e.getTitulos());
    view.getContext().startActivity(intent);
  }
}
