package com.tfg.mifeed.controlador.Adaptadores.AdaptadoresPrensa;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.tfg.mifeed.R;
import com.tfg.mifeed.controlador.activities.Activities.Prensa.NoticiaActivity;
import com.tfg.mifeed.controlador.firebase.FirebaseGestionUsuario;
import com.tfg.mifeed.controlador.firebase.FirebaseNoticias;
import com.tfg.mifeed.controlador.firebase.FirebaseServices;
import com.tfg.mifeed.controlador.utilidades.CheckConexion;

import java.util.ArrayList;

public class AdaptadorListaArticulosEtiqueta extends RecyclerView.Adapter<AdaptadorListaArticulosEtiqueta.ViewHolderEtiqueta>{

    private ArrayList<String> urls;
    private ArrayList<String> nombresWebs;
    private String nombreEtiqueta;
    private Context context;
    public FirebaseNoticias firebaseGestionUsuario;

    public AdaptadorListaArticulosEtiqueta(ArrayList<String> urls, ArrayList<String> nombres,String nombreEtiqueta, View v, Context c){
        this.nombresWebs = nombres;
        this.urls = urls;
        this.context = c;
        this.nombreEtiqueta = nombreEtiqueta;
        this.firebaseGestionUsuario = new FirebaseNoticias();
    }


    @NonNull
    @Override
    public ViewHolderEtiqueta onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View vista = inflater.inflate(R.layout.elemento_lista_articulos,parent,false);
        ViewHolderEtiqueta viewHolderEtiqueta = new ViewHolderEtiqueta(vista);
        return viewHolderEtiqueta;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderEtiqueta holder, int position) {
        TextView titulo = holder.tituloArticulo;
        TextView url = holder.urlArticulo;
        ImageView eliminar = holder.btnEliminar;
        LinearLayout articulo = holder.articulo;
        titulo.setText(nombresWebs.get(position));
        url.setText(urls.get(position));
        eliminar.setOnClickListener(v1 -> {
            if (!CheckConexion.getEstadoActual(context)) {
                Toast.makeText(context, R.string.errConn, Toast.LENGTH_LONG).show();
            } else {
                eliminarWeb(position);
            }
        });
        articulo.setOnClickListener(v1 -> {
            if (!CheckConexion.getEstadoActual(context)) {
                Toast.makeText(context, R.string.errConn, Toast.LENGTH_LONG).show();
            } else {
                abrirUrl(urls.get(position));
            }
        });
    }
    private void eliminarWeb(int posicion) {
        FirebaseNoticias.eliminarUrlLista(urls.get(posicion),nombreEtiqueta);
        urls.remove(posicion);
        nombresWebs.remove(posicion);
        notifyItemRemoved(posicion);
        notifyItemRangeChanged(posicion,urls.size());
    }

    private void abrirUrl(String url){
        Intent intent = new Intent(context, NoticiaActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("enlace", url);
        context.startActivity(intent);
    }

    @Override
    public int getItemCount() {
        return urls.size();
    }

    public static class ViewHolderEtiqueta extends RecyclerView.ViewHolder{

        TextView tituloArticulo,urlArticulo;
        ImageView btnEliminar;
        LinearLayout articulo;

        public ViewHolderEtiqueta(@NonNull View itemView) {
            super(itemView);
            tituloArticulo = itemView.findViewById(R.id.tituloArticulo);
            urlArticulo = itemView.findViewById(R.id.urlArticulo);
            btnEliminar = itemView.findViewById(R.id.eliminarWeb);
            articulo = itemView.findViewById(R.id.toUrl);
        }
    }
}
