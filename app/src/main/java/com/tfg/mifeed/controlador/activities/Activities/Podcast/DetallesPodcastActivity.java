package com.tfg.mifeed.controlador.activities.Activities.Podcast;

import static android.text.Html.fromHtml;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.tfg.mifeed.R;
import com.tfg.mifeed.controlador.adaptadores.AdaptadorListaEpisodiosPodcast;
import com.tfg.mifeed.controlador.conexioPodcastApi.ApiPodcastConn;
import com.tfg.mifeed.controlador.firebase.FirebaseServices;
import com.tfg.mifeed.modelo.Episodio;
import com.tfg.mifeed.modelo.RespuestaListaPodcast;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetallesPodcastActivity extends AppCompatActivity {

    private RecyclerView listaEpisodios;
    private ImageView logoPodcast,addPodcast;
    private TextView descripcion, errBusqueda;
    private ProgressBar carga;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalles_podcast);
        String idPodcast = getIntent().getExtras().getString("idPodcast");
        String urlImagen = getIntent().getExtras().getString("urlImagen");
        String txtDescripcion = String.valueOf(fromHtml(getIntent().getExtras().getString("descripcion"),0));
        descripcion = findViewById(R.id.descripcionPodcast);
        logoPodcast = findViewById(R.id.logoPodcast);
        addPodcast = findViewById(R.id.btnAddBiblioteca);
        listaEpisodios = findViewById(R.id.listaEpisodios);
        errBusqueda = findViewById(R.id.errLista);
        carga = findViewById(R.id.cargaLista);
        carga.setVisibility(View.VISIBLE);
        Glide.with(DetallesPodcastActivity.this)
                .load(urlImagen)
                .override(400, 300)
                .into(logoPodcast);
        descripcion.setText(txtDescripcion);
        getPodcast(idPodcast);
        addPodcast.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseServices.addPodcastBiblioteca(idPodcast,DetallesPodcastActivity.this);
            }
        });
    }

    private void getPodcast(String id){
        ApiPodcastConn.getApiConnInterfacePodcast().getEpisodiosConid(id,"recent_first").enqueue(new Callback<RespuestaListaPodcast>() {
            @Override
            public void onResponse(Call<RespuestaListaPodcast> call, Response<RespuestaListaPodcast> response) {
                ArrayList<Episodio> listaCapitulos =new ArrayList<>();
                listaCapitulos.addAll(response.body().getEpisodes());
                AdaptadorListaEpisodiosPodcast adaptadorListaEpisodiosPodcast = new AdaptadorListaEpisodiosPodcast(listaCapitulos,DetallesPodcastActivity.this);
                LinearLayoutManager linearLayoutManager =
                        new LinearLayoutManager(DetallesPodcastActivity.this, LinearLayoutManager.VERTICAL, false);
                listaEpisodios.setAdapter(adaptadorListaEpisodiosPodcast);
                listaEpisodios.setLayoutManager(linearLayoutManager);
                carga.setVisibility(View.GONE);
                errBusqueda.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<RespuestaListaPodcast> call, Throwable t) {
                carga.setVisibility(View.GONE);
                errBusqueda.setVisibility(View.VISIBLE);
            }
        });
    }
}