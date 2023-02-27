package com.tfg.mifeed.controlador.activities.Activities.Prensa.FragmentsPrensa;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.tfg.mifeed.R;
import com.tfg.mifeed.controlador.Adaptadores.AdaptadoresPrensa.AdaptadorListaNoticias;
import com.tfg.mifeed.controlador.conexionNewsApi.ApiConn;
import com.tfg.mifeed.controlador.firebase.FirebaseServices;
import com.tfg.mifeed.controlador.utilidades.CheckConexion;
import com.tfg.mifeed.modelo.Noticia;
import com.tfg.mifeed.modelo.RespuestaListaNoticias;

import java.util.ArrayList;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CategoriasFragment extends Fragment {

    private final String API_KEY = "c3afd5ea3f5548adbe7afc7e21c4c0bf";
    public static ProgressBar cargaEleccion;

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =inflater.inflate(R.layout.fragment_categorias, container, false);
        cargaEleccion = view.findViewById(R.id.cargaEleccion);

        if(!CheckConexion.getEstadoActual(view.getContext())){
            Toast.makeText(view.getContext(),R.string.errConn,Toast.LENGTH_LONG).show();
            cargaEleccion.setVisibility(View.GONE);
        }else{
            FirebaseServices.getTemasUsuario(view,"categorias");
        }
        return view;
    }

    private void getNoticias(String tema, View contenedor) {
        ArrayList<Noticia> respuesta = new ArrayList<>();
        RecyclerView lista = contenedor.findViewById(R.id.RecycleviewNoticias);
        AdaptadorListaNoticias adaptadorListaNoticias = new AdaptadorListaNoticias(contenedor.getContext().getApplicationContext(),respuesta,contenedor);
        LinearLayoutManager linearLayoutManager =
                new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        lista.setLayoutManager(linearLayoutManager);
        lista.setAdapter(adaptadorListaNoticias);
        TextView err = contenedor.findViewById(R.id.errConexion);
        ProgressBar carga = contenedor.findViewById(R.id.cargaCategorias);
        carga.setVisibility(View.VISIBLE);
        ApiConn.getApiConnInterface().getNoticiasPorTema(100,tema,API_KEY).enqueue(new Callback<RespuestaListaNoticias>() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onResponse(@NonNull Call<RespuestaListaNoticias> call, @NonNull Response<RespuestaListaNoticias> response) {
                cargaEleccion.setVisibility(View.GONE);
                if (response.isSuccessful() && Objects.requireNonNull(response.body()).getNoticias().size()>0){
                    respuesta.clear();
                    respuesta.addAll(response.body().getNoticias());
                    adaptadorListaNoticias.notifyDataSetChanged();
                    err.setVisibility(View.GONE);
                    carga.setVisibility(View.GONE);
                }else{
                    err.setVisibility(View.VISIBLE);
                    carga.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(@NonNull Call<RespuestaListaNoticias> call, @NonNull Throwable t) {
                Toast.makeText(contenedor.getContext(),R.string.errConn,Toast.LENGTH_LONG).show();
            }
        });
        carga.setVisibility(View.GONE);
    }

    public int traducirCategorias(String catIngles){
        switch (catIngles) {
            case "business":
                return R.string.txtNegocios;
            case "entertainment":
                return R.string.txtOcio;
            case "health":
                return R.string.txtMedicina;
            case "science":
                return R.string.txtCiencia;
            case "sports":
                return R.string.txtDeporte;
            case "technology":
                return R.string.txtTecnologia;
            default:
                Log.d("valor",catIngles);
                return R.string.errConn;
        }
    }

    public void rellenarCategorias(View vContenedor, ArrayList<String> temas, String res){
        LinearLayout.LayoutParams layoutParams =
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(0, 10, 30, 5);
        LinearLayout layoutCategorias = vContenedor.findViewById(R.id.categorias);
        layoutCategorias.removeAllViews();
        if(res == "true"){
            ArrayList<String> categorias = temas;
            for (int i = 0; i < categorias.size(); i++) {
                GradientDrawable shape = new GradientDrawable();
                final Button btCategory = new Button(vContenedor.getContext());
                shape.setShape(GradientDrawable.RECTANGLE);
                shape.setColor(Color.BLACK);
                shape.setCornerRadius(30);
                btCategory.setBackground(shape);
                btCategory.setText(traducirCategorias(categorias.get(i)));
                btCategory.setTextColor(Color.WHITE);
                btCategory.setTextSize(16f);
                btCategory.setAllCaps(false);
                btCategory.setLayoutParams(layoutParams);
                btCategory.setTag(categorias.get(i));
                btCategory.setId(i);
                btCategory.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        cargaEleccion.setVisibility(View.VISIBLE);
                        setAccion((String) v.getTag(),vContenedor);
                    }
                });
                layoutCategorias.addView(btCategory);
            }
        }
    }

    private void setAccion(String tag, View vista) {
        switch (tag) {
            case "business":
                getNoticias("business",vista);
                break;
            case "entertainment":
                getNoticias("entertainment",vista);
                break;
            case "health":
                getNoticias("health",vista);
                break;
            case "science":
                getNoticias("science",vista);
                break;
            case "sports":
                getNoticias("sports",vista);
                break;
            case "technology":
                getNoticias("technology",vista);
                break;
        }
    }
}