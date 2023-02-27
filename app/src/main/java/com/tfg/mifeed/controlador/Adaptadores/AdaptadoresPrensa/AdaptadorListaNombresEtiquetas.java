package com.tfg.mifeed.controlador.Adaptadores.AdaptadoresPrensa;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.tfg.mifeed.R;

import java.util.ArrayList;

public class AdaptadorListaNombresEtiquetas extends BaseAdapter {

    private ArrayList<String> listaNombres;

    public AdaptadorListaNombresEtiquetas(ArrayList<String> lista){
        this.listaNombres = lista;
    }

    @Override
    public int getCount() {
        return listaNombres.size();
    }

    @Override
    public Object getItem(int position) {
        return listaNombres.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View vista = inflater.inflate(R.layout.elemento_lista_nombre,parent,false);
        TextView nombreEtiqueta = vista.findViewById(R.id.nombreEtiqueta);
        nombreEtiqueta.setText(listaNombres.get(position));
        return vista;
    }
}
