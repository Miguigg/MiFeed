package com.tfg.mifeed.controlador.adaptadores;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.tfg.mifeed.R;
import com.tfg.mifeed.controlador.activities.Activities.Prensa.NoticiaActivity;
import com.tfg.mifeed.modelo.Noticia;

import java.util.ArrayList;

public class AdaptadorListaNoticias extends RecyclerView.Adapter<AdaptadorListaNoticias.ViewHolderListaNoticias> {

    private Context context;
    private ArrayList<Noticia> listaNoticias;
    private View v;

    public AdaptadorListaNoticias(Context c, ArrayList<Noticia> noticias, View v){
        this.listaNoticias = noticias;
        this.context = c;
        this.v = v;
    }

    @NonNull
    @Override
    public ViewHolderListaNoticias onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View vista = inflater.inflate(R.layout.elemento_lista_noticias,parent,false);
        ViewHolderListaNoticias viewHolderListaNoticias = new ViewHolderListaNoticias(vista);
        return viewHolderListaNoticias;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderListaNoticias holder, int pos) {
        String[] titulos = adaptarTexto(listaNoticias.get(pos).getTitle());
        switch (titulos.length) {
            case 0:
                Log.d("Vacio", "No hay datos");
                break;
            case 1:
                holder.titulo.setText(listaNoticias.get(pos).getTitle());
                holder.contenido.setText(listaNoticias.get(pos).getDescription());
                holder.autor.setText(listaNoticias.get(pos).getAuthor());
                Glide.with(context)
                        .load(listaNoticias.get(pos).getUrlToImage())
                        .override(900, 800)
                        .into( holder.foto);
                break;
            case 2:
                holder.titulo.setText(titulos[0]);
                holder.contenido.setText(listaNoticias.get(pos).getDescription());
                holder.autor.setText(titulos[1]);
                Glide.with(context)
                        .load(listaNoticias.get(pos).getUrlToImage())
                        .override(900, 800)
                        .into(holder.foto);
                break;
        }
        if (getItemCount() > 0) {
      holder.toNoticia.setOnClickListener(
          v -> {
            Intent intent = new Intent(context, NoticiaActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("enlace", listaNoticias.get(pos).getUrl());
            context.startActivity(intent);
          });
        }
    }

    public String[] adaptarTexto(String original) {
        String[] titulos = original.split("-");
        return titulos;
    }

    @Override
    public int getItemCount() {
        return listaNoticias.size();
    }

    public static class ViewHolderListaNoticias extends RecyclerView.ViewHolder{
        TextView titulo, contenido, autor;
        ImageView foto;
        LinearLayout toNoticia;
        public ViewHolderListaNoticias(@NonNull View itemView) {
            super(itemView);
            titulo = itemView.findViewById(R.id.titulo);
            contenido = itemView.findViewById(R.id.content);
            autor = itemView.findViewById(R.id.medio);
            foto = itemView.findViewById(R.id.foto);
            toNoticia = itemView.findViewById(R.id.toNoticia);
        }
    }
}
