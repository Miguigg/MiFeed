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

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.tfg.mifeed.R;
import com.tfg.mifeed.controlador.activities.Activities.Podcast.DetallesPodcastActivity;
import com.tfg.mifeed.controlador.firebase.FirebaseServices;
import com.tfg.mifeed.modelo.Episodio;

import java.io.IOException;
import java.util.ArrayList;

public class AdaptadorListaEpisodios
    extends RecyclerView.Adapter<AdaptadorListaEpisodios.ViewHolder> {

  ArrayList<Episodio> listaEpisodios;
  Context context;
  MediaPlayer mediaPlayer;

  public AdaptadorListaEpisodios(ArrayList<Episodio> listaEpisodios, Context context) {
    this.context = context;
    this.listaEpisodios = listaEpisodios;
  }

  @NonNull
  @Override
  public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    Context context = parent.getContext();
    LayoutInflater inflater = LayoutInflater.from(context);
    View view = inflater.inflate(R.layout.elemento_lista_episodios, parent, false);
    AdaptadorListaEpisodios.ViewHolder viewHolder = new ViewHolder(view);
    return viewHolder;
  }

  @Override
  public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
    TextView titulo = holder.titulo;
    titulo.setText(fromHtml(listaEpisodios.get(position).getTitle_original(), 0));
    ImageView imageView = holder.imagenEpisodio;
    Glide.with(context)
        .load(listaEpisodios.get(position).getImage())
        .override(400, 300)
        .into(imageView);
    ImageView play = holder.play;
    ImageView pause = holder.pausa;
    ImageView masTarde = holder.masTarde;
    play.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            play.setVisibility(View.GONE);
            pause.setVisibility(View.VISIBLE);
            reproducir(listaEpisodios.get(holder.getAdapterPosition()).getAudio());
          }
        });
    pause.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            play.setVisibility(View.VISIBLE);
            pause.setVisibility(View.GONE);
            parar();
          }
        });
    masTarde.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            addIdEpisodio(listaEpisodios.get(holder.getAdapterPosition()), String.valueOf(fromHtml(listaEpisodios.get(holder.getAdapterPosition()).getTitle_original(), 0)));
          }
        });
    imageView.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            Intent intent = new Intent(context, DetallesPodcastActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra(
                "urlImagen", listaEpisodios.get(holder.getAdapterPosition()).getImage());
            intent.putExtra(
                "descripcion",
                listaEpisodios.get(holder.getAdapterPosition()).getDescription_original());
            intent.putExtra(
                "idPodcast", listaEpisodios.get(holder.getAdapterPosition()).getPodcast().getId());
            context.startActivity(intent);
          }
        });
  }

  private void addIdEpisodio(Episodio episodio, String titulo) {
    FirebaseServices.addParaMasTarde(episodio, titulo ,context);
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
    ImageView imagenEpisodio, pausa, play, recordatorio, masTarde;
    TextView titulo;

    public ViewHolder(@NonNull View itemView) {
      super(itemView);
      imagenEpisodio = itemView.findViewById(R.id.imagenEpisodio);
      pausa = itemView.findViewById(R.id.parar);
      play = itemView.findViewById(R.id.reproducir);
      recordatorio = itemView.findViewById(R.id.recordatorio);
      masTarde = itemView.findViewById(R.id.masTarde);
      titulo = itemView.findViewById(R.id.tituloEpisodio);
    }
  }
}
