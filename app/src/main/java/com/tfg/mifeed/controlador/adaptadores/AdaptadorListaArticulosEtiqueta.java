package com.tfg.mifeed.controlador.adaptadores;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.tfg.mifeed.R;
import com.tfg.mifeed.controlador.activities.Activities.Prensa.NoticiaActivity;
import com.tfg.mifeed.controlador.firebase.FirebaseServices;

import java.util.ArrayList;

public class AdaptadorListaArticulosEtiqueta extends RecyclerView.Adapter<AdaptadorListaArticulosEtiqueta.ViewHolder>{

    private ArrayList<String> urls;
    private ArrayList<String> nombresWebs;
    private String nombreEtiqueta;
    private View v;
    private Context context;

    public AdaptadorListaArticulosEtiqueta(ArrayList<String> urls, ArrayList<String> nombres,String nombreEtiqueta, View v, Context c){
        this.nombresWebs = nombres;
        this.urls = urls;
        this.v = v;
        this.context = c;
        this.nombreEtiqueta = nombreEtiqueta;
    }


    @NonNull
    @Override
    public AdaptadorListaArticulosEtiqueta.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View vista = inflater.inflate(R.layout.elemento_lista_articulos,parent,false);
        AdaptadorListaArticulosEtiqueta.ViewHolder viewHolder = new AdaptadorListaArticulosEtiqueta.ViewHolder(vista);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull AdaptadorListaArticulosEtiqueta.ViewHolder holder, int position) {
        TextView titulo = holder.tituloArticulo;
        TextView url = holder.urlArticulo;
        ImageView eliminar = holder.btnEliminar;
        LinearLayout articulo = holder.articulo;
        titulo.setText(nombresWebs.get(position));
        url.setText(urls.get(position));
        eliminar.setOnClickListener(v1 -> {
            eliminarWeb(position);
        });
        articulo.setOnClickListener(v1 -> {
            abrirUrl(urls.get(position));
        });
    }
    private void eliminarWeb(int posicion) {
        FirebaseServices.eliminarUrlLista(urls.get(posicion),nombresWebs.get(posicion),nombreEtiqueta);
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

    public static class ViewHolder extends RecyclerView.ViewHolder{

        TextView tituloArticulo,urlArticulo;
        ImageView btnEliminar;
        LinearLayout articulo;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tituloArticulo = itemView.findViewById(R.id.tituloArticulo);
            urlArticulo = itemView.findViewById(R.id.urlArticulo);
            btnEliminar = itemView.findViewById(R.id.eliminarWeb);
            articulo = itemView.findViewById(R.id.toUrl);
        }
    }
}
