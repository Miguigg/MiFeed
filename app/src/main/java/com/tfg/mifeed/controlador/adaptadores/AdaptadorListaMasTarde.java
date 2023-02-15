package com.tfg.mifeed.controlador.adaptadores;

import static android.text.Html.fromHtml;

import android.content.Context;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.tfg.mifeed.R;
import com.tfg.mifeed.controlador.activities.Activities.Podcast.DetallesPodcastActivity;
import com.tfg.mifeed.controlador.firebase.FirebaseServices;
import com.tfg.mifeed.controlador.utilidades.CheckConexion;
import com.tfg.mifeed.modelo.Episodio;

import java.io.IOException;
import java.util.ArrayList;

public class AdaptadorListaMasTarde extends RecyclerView.Adapter<AdaptadorListaMasTarde.ViewHolder>{

    ArrayList<Episodio> listaEpisodios;
    Context context;
    MediaPlayer mediaPlayer;
    boolean reproduciendo;

    public AdaptadorListaMasTarde(ArrayList<Episodio> listaEpisodios, Context context){
        this.context = context;
        this.listaEpisodios = listaEpisodios;
        this.reproduciendo = false;
    }

    @NonNull
    @Override
    public AdaptadorListaMasTarde.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.elemento_lista_mas_tade, parent, false);
        AdaptadorListaMasTarde.ViewHolder viewHolder = new AdaptadorListaMasTarde.ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull AdaptadorListaMasTarde.ViewHolder holder, int position) {
        TextView titulo = holder.titulo;
        titulo.setText(fromHtml(listaEpisodios.get(position).getTitle(), 0));
        ImageView imageView = holder.imagenEpisodio;
        Glide.with(context)
                .load(listaEpisodios.get(position).getImage())
                .override(400, 300)
                .into(imageView);
        ImageView play = holder.play;
        ImageView eliminar =  holder.eliminar;

        play.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!CheckConexion.getEstadoActual(context)) {
                            Toast.makeText(context, R.string.errConn, Toast.LENGTH_LONG).show();
                        } else {
                            if(reproduciendo == false){
                                reproduciendo = true;
                                play.setImageResource(R.drawable.ic_parar_episodio);
                                reproducir(listaEpisodios.get(holder.getAdapterPosition()).getAudio());
                            }else{
                                reproduciendo = false;
                                play.setImageResource(R.drawable.ic_rep_episodio);
                                parar();
                            }
                        }
                    }
                });

        imageView.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!CheckConexion.getEstadoActual(context)) {
                            Toast.makeText(context, R.string.errConn, Toast.LENGTH_LONG).show();
                        } else {
                            Intent intent = new Intent(context, DetallesPodcastActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.putExtra(
                                    "urlImagen", listaEpisodios.get(holder.getAdapterPosition()).getImage());
                            intent.putExtra(
                                    "descripcion",
                                    listaEpisodios.get(holder.getAdapterPosition()).getDescription());
                            intent.putExtra(
                                    "idPodcast", listaEpisodios.get(holder.getAdapterPosition()).getId());
                            intent.putExtra("titulo",listaEpisodios.get(holder.getAdapterPosition()).getTituloPodcast());
                            context.startActivity(intent);
                        }
                    }
                });
        eliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!CheckConexion.getEstadoActual(context)) {
                    Toast.makeText(context, R.string.errConn, Toast.LENGTH_LONG).show();
                } else {
                    FirebaseServices.eliminarPodcastMastarde(listaEpisodios.get(holder.getAdapterPosition()).getAudio(),context);
                    listaEpisodios.remove(holder.getAdapterPosition());
                    notifyItemRemoved(holder.getAdapterPosition());
                    notifyItemRangeChanged(holder.getAdapterPosition(),listaEpisodios.size());
                }
            }
        });
    }

    private void reproducir(String url) {
        mediaPlayer = new MediaPlayer();
        AudioAttributes atr =
                new AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                        .build();
        mediaPlayer.setAudioAttributes(atr);
        try {
            mediaPlayer.setDataSource(url);
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void parar() {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            mediaPlayer.reset();
            mediaPlayer.release();
        }
    }

    @Override
    public int getItemCount() {
        return listaEpisodios.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imagenEpisodio, pausa, play, recordatorio, eliminar;
        TextView titulo;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imagenEpisodio = itemView.findViewById(R.id.imagenEpisodio2);
            play = itemView.findViewById(R.id.reproducir2);
            recordatorio = itemView.findViewById(R.id.recordatorio2);
            eliminar = itemView.findViewById(R.id.btnEliminar);
            titulo = itemView.findViewById(R.id.tituloEpisodio2);
        }
    }

}
