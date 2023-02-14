package com.tfg.mifeed.controlador.adaptadores;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.tfg.mifeed.R;
import com.tfg.mifeed.modelo.Podcast;

import java.util.ArrayList;

public class AdaptadorBibliotecaPodcast extends RecyclerView.Adapter<AdaptadorBibliotecaPodcast.ViewHolder>{

    ArrayList<Podcast> listaPodcast;
    Context c;

    public AdaptadorBibliotecaPodcast(ArrayList<Podcast> listaPodcast, Context c){
        this.c = c;
        this.listaPodcast = listaPodcast;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.elemento_lista_biblioteca, parent, false);
        AdaptadorBibliotecaPodcast.ViewHolder viewHolder = new ViewHolder(view);
        return  viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ImageView logo = holder.imagenBiblioteca;
        TextView titulo = holder.titulo;
        Glide.with(c)
                .load(listaPodcast.get(position).getImage())
                .override(400, 300)
                .into(logo);
        titulo.setText(listaPodcast.get(position).getTitle_original());
    }

    @Override
    public int getItemCount() {
        return listaPodcast.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imagenBiblioteca;
        TextView titulo;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imagenBiblioteca = itemView.findViewById(R.id.imagenBiblioteca);
            titulo = itemView.findViewById(R.id.tituloPodcast);
        }
    }
}
