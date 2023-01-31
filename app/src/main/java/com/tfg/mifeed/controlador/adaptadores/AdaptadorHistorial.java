package com.tfg.mifeed.controlador.adaptadores;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.tfg.mifeed.R;
import com.tfg.mifeed.controlador.activities.Activities.Prensa.NoticiaActivity;
import com.tfg.mifeed.controlador.firebase.FirebaseServices;

import java.util.ArrayList;

public class AdaptadorHistorial extends RecyclerView.Adapter<AdaptadorHistorial.ViewHolder>{

    private ArrayList<String> listaUrls;
    private Context context;

    public AdaptadorHistorial(ArrayList<String> lista, Context c){
        this.listaUrls = lista;
        this.context = c;
    }

    @NonNull
    @Override
    public AdaptadorHistorial.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View vista = inflater.inflate(R.layout.elemento_lista_historial,parent,false);
        AdaptadorHistorial.ViewHolder viewHolder = new  AdaptadorHistorial.ViewHolder(vista);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull AdaptadorHistorial.ViewHolder holder, int position) {
        TextView url = holder.url;
        ImageView btnEliminar = holder.eliminar;
        url.setText(listaUrls.get(position));

        url.setOnClickListener(v -> {
            toWeb(listaUrls.get(position));
        });

        btnEliminar.setOnClickListener(v -> {
            eliminarHistorial(position);
        });
    }

    public void eliminarHistorial(int pos){
        FirebaseServices.eliminarElementoHistorial(listaUrls.get(pos));
        listaUrls.remove(pos);
        notifyItemRemoved(pos);
        notifyItemRangeChanged(pos,listaUrls.size());
    }

    public void toWeb(String url){
        Intent intent = new Intent(context, NoticiaActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("enlace", url);
        context.startActivity(intent);
    }

    @Override
    public int getItemCount() {
        return listaUrls.size();
    }
    public static class ViewHolder extends RecyclerView.ViewHolder{

        TextView url;
        ImageView eliminar;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            url = itemView.findViewById(R.id.url);
            eliminar = itemView.findViewById(R.id.eliminarElemento);
        }
    }
}
