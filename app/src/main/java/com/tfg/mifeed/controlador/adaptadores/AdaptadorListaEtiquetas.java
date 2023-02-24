package com.tfg.mifeed.controlador.adaptadores;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.tfg.mifeed.R;
import com.tfg.mifeed.controlador.activities.Activities.Prensa.FragmentsPrensa.EtiquetasFragment;
import com.tfg.mifeed.controlador.firebase.FirebaseServices;
import com.tfg.mifeed.modelo.Etiqueta;

import java.util.ArrayList;

public class AdaptadorListaEtiquetas extends RecyclerView.Adapter<AdaptadorListaEtiquetas.ViewHolderListaEtiquetas> {

    private ArrayList<Etiqueta> listaEtiquetas;

    private View view;

    public AdaptadorListaEtiquetas(ArrayList<Etiqueta> listaEtiquetas,View view){
        this.listaEtiquetas = listaEtiquetas;
        this.view = view;
    }

    @NonNull
    @Override
    public ViewHolderListaEtiquetas onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View vista = inflater.inflate(R.layout.elemento_lista_etiquetas,parent,false);
        ViewHolderListaEtiquetas viewHolderListaEtiquetas = new ViewHolderListaEtiquetas(vista);
        return viewHolderListaEtiquetas;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderListaEtiquetas holder, int position) {
        EtiquetasFragment etiquetasFragment = new EtiquetasFragment();
        TextView titulo = holder.tituloEtiqueta;
        titulo.setText(listaEtiquetas.get(position).getTituloEtiqueta());
        ConstraintLayout elemetoListaEtiquetas = holder.elemetoListaEtiquetas;
        ImageView eliminarEtiqueta = holder.btnEliminarEtiqueta;

        elemetoListaEtiquetas.setOnClickListener(v -> {
            etiquetasFragment.abrirEtiqueta(listaEtiquetas.get(position),view);
        });
        eliminarEtiqueta.setOnClickListener(v->{
            eliminarEtiqueta(listaEtiquetas.get(position).getTituloEtiqueta(),position);
        });
    }

    private void eliminarEtiqueta(String nombreEtiqueta, int pos) {
        listaEtiquetas.remove(pos);
        notifyItemRemoved(pos);
        notifyItemRangeChanged(pos,listaEtiquetas.size());
        FirebaseServices.deleteEtiqueta(nombreEtiqueta,view);
    }

    @Override
    public int getItemCount() {
        return listaEtiquetas.size();
    }

    public static class ViewHolderListaEtiquetas extends RecyclerView.ViewHolder{
        private TextView tituloEtiqueta;
        private ConstraintLayout elemetoListaEtiquetas;

        private ImageView btnEliminarEtiqueta;
        public ViewHolderListaEtiquetas(@NonNull View itemView) {
            super(itemView);
            tituloEtiqueta = itemView.findViewById(R.id.tituloEtiqueta);
            elemetoListaEtiquetas = itemView.findViewById(R.id.elemetoListaEtiquetas);
            btnEliminarEtiqueta = itemView.findViewById(R.id.btnEliminarEtiqueta);
        }
    }
}
