package com.tfg.mifeed.controlador.adaptadores;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.tfg.mifeed.R;
import com.tfg.mifeed.controlador.activities.Activities.GestionCuenta.SeleccionMediosActivity;

import java.util.ArrayList;

public class AdaptadorListaMedios extends RecyclerView.Adapter<AdaptadorListaMedios.ViewHolder> {
    private ArrayList<String> medios;
    private ArrayList<String> dominios;

    public AdaptadorListaMedios(ArrayList<String> medios, ArrayList<String> dominios){
        this.dominios = dominios;
        this.medios = medios;
    }

    @NonNull
    @Override
    public AdaptadorListaMedios.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View vista = inflater.inflate(R.layout.elemento_lista_medios,parent,false);
        ViewHolder viewHolder = new ViewHolder(vista);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SeleccionMediosActivity seleccionMediosActivity = new SeleccionMediosActivity();
        ImageView imageView = holder.imageView;
        TextView nombre = holder.nombre;
        TextView dominio = holder.dominio;
        @SuppressLint("UseSwitchCompatOrMaterialCode") Switch swMedio = holder.swMedio;
        imageView.setImageResource(R.drawable.ic_periodico_grande);
        nombre.setText(medios.get(position));
        dominio.setText(dominios.get(position));

        swMedio.setOnClickListener(v -> {
            if (swMedio.isChecked()){
                seleccionMediosActivity.insertMedio(dominios.get(position),medios.get(position));
            }else{
                seleccionMediosActivity.deleteMedio(dominios.get(position),medios.get(position));
            }
        });
    }

    @Override
    public int getItemCount() {
        return medios.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        public ImageView imageView;
        public TextView nombre,dominio;
        @SuppressLint("UseSwitchCompatOrMaterialCode")
        public Switch swMedio;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imgMedio);
            nombre = itemView.findViewById(R.id.nombreMedio);
            dominio = itemView.findViewById(R.id.dominioMedio);
            swMedio = itemView.findViewById(R.id.switchMedio);
        }
    }
}
