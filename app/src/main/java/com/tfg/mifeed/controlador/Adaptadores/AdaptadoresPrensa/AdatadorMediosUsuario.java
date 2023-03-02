package com.tfg.mifeed.controlador.Adaptadores.AdaptadoresPrensa;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.tfg.mifeed.R;
import com.tfg.mifeed.controlador.activities.Activities.BienvenidaActivity;

import java.util.ArrayList;

public class AdatadorMediosUsuario extends RecyclerView.Adapter<AdatadorMediosUsuario.ViewHolderMediosUsuario> {
    private final ArrayList<String> lista;

    public AdatadorMediosUsuario(ArrayList<String> lista){
        this.lista = lista;
    }

    @NonNull
    @Override
    public ViewHolderMediosUsuario onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View vista = inflater.inflate(R.layout.elemento_lista_medios_usuario,parent,false);
        return new ViewHolderMediosUsuario(vista);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderMediosUsuario holder, int position) {
        ImageView imageView = holder.imageView;
        TextView nombre = holder.nombre;
        switch (lista.get(position)){
            case "science":
                nombre.setText(R.string.txtCiencia);
                imageView.setImageResource(R.drawable.ic_ciencia);
                break;
            case "health":
                nombre.setText(R.string.txtMedicina);
                imageView.setImageResource(R.drawable.ic_salud);
                break;
            case "technology":
                nombre.setText(R.string.txtTecnologia);
                imageView.setImageResource(R.drawable.ic_tecnologia);
                break;
            case "entertainment":
                nombre.setText(R.string.txtOcio);
                imageView.setImageResource(R.drawable.ic_ocio);
                break;
            case "business":
                nombre.setText(R.string.txtNegocios);
                imageView.setImageResource(R.drawable.ic_negocios);
                break;
            case "sports":
                nombre.setText(R.string.txtDeporte);
                imageView.setImageResource(R.drawable.ic_deporte);
                break;
            default:
                nombre.setText(lista.get(position));
                imageView.setImageResource(R.drawable.ic_prensa);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return lista.size();
    }

    public static class ViewHolderMediosUsuario extends RecyclerView.ViewHolder{
        public ImageView imageView;
        public TextView nombre;

        public ViewHolderMediosUsuario(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imgMedioUsuario);
            nombre = itemView.findViewById(R.id.nombreMedioUsuario);
        }
    }
}
