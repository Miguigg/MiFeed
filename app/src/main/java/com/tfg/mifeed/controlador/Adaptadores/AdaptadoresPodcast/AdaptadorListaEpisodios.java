package com.tfg.mifeed.controlador.Adaptadores.AdaptadoresPodcast;

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
import com.tfg.mifeed.controlador.activities.Activities.Podcast.CreacionRecordatorioActivity;
import com.tfg.mifeed.controlador.activities.Activities.Podcast.DetallesPodcastActivity;
import com.tfg.mifeed.controlador.firebase.FirebaseServices;
import com.tfg.mifeed.controlador.utilidades.CheckConexion;
import com.tfg.mifeed.modelo.Episodio;

import java.io.IOException;
import java.util.ArrayList;

public class AdaptadorListaEpisodios
    extends RecyclerView.Adapter<AdaptadorListaEpisodios.ViewHolderEpisodios> {

  private ArrayList<Episodio> listaEpisodios;
  private Context context;
  private MediaPlayer mediaPlayer;

  private boolean repoduciendo;

  public AdaptadorListaEpisodios(ArrayList<Episodio> listaEpisodios, Context context) {
    this.context = context;
    this.listaEpisodios = listaEpisodios;
    this.repoduciendo = false;
  }

  @NonNull
  @Override
  public ViewHolderEpisodios onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    Context context = parent.getContext();
    LayoutInflater inflater = LayoutInflater.from(context);
    View view = inflater.inflate(R.layout.elemento_lista_episodios, parent, false);
    ViewHolderEpisodios viewHolderEpisodios = new ViewHolderEpisodios(view);
    return viewHolderEpisodios;
  }

  @Override
  public void onBindViewHolder(@NonNull ViewHolderEpisodios holder, int position) {
    TextView titulo = holder.titulo;
    titulo.setText(fromHtml(listaEpisodios.get(position).getTitle_original(), 0));
    ImageView imageView = holder.imagenEpisodio;
    Glide.with(context)
        .load(listaEpisodios.get(position).getImage())
        .override(400, 300)
        .into(imageView);
    ImageView play = holder.play;
    ImageView masTarde = holder.masTarde;
    ImageView recordatorio = holder.recordatorio;

    play.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            if (!CheckConexion.getEstadoActual(context)) {
              Toast.makeText(context, R.string.errConn, Toast.LENGTH_LONG).show();
            } else {
              if (!repoduciendo) {
                repoduciendo = true;
                play.setImageResource(R.drawable.ic_parar_episodio);
                reproducir(listaEpisodios.get(holder.getAdapterPosition()).getAudio());
              } else {
                repoduciendo = false;
                play.setImageResource(R.drawable.ic_rep_episodio);
                parar();
              }
            }
          }
        });

    masTarde.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            if (!CheckConexion.getEstadoActual(context)) {
              Toast.makeText(context, R.string.errConn, Toast.LENGTH_LONG).show();
            } else {
              addIdEpisodio(
                  listaEpisodios.get(holder.getAdapterPosition()),
                  String.valueOf(
                      fromHtml(
                          listaEpisodios.get(holder.getAdapterPosition()).getTitle_original(), 0)));
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
                  listaEpisodios.get(holder.getAdapterPosition()).getDescription_original());
              intent.putExtra(
                  "idPodcast",
                  listaEpisodios.get(holder.getAdapterPosition()).getPodcast().getId());
              intent.putExtra(
                  "titulo",
                  listaEpisodios.get(holder.getAdapterPosition()).getPodcast().getTitle_original());
              context.startActivity(intent);
            }
          }
        });
    recordatorio.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        Intent intent = new Intent(context, CreacionRecordatorioActivity.class);
        intent.putExtra("nombreEpisodio",listaEpisodios.get(holder.getAdapterPosition()).getTitle_original());
        intent.putExtra("urlAudio",listaEpisodios.get(holder.getAdapterPosition()).getAudio());
        intent.putExtra("urlImagen",listaEpisodios.get(holder.getAdapterPosition()).getImage());
        intent.putExtra("idPodcast",listaEpisodios.get(holder.getAdapterPosition()).getPodcast().getId());
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
      }
    });
  }

  private void addIdEpisodio(Episodio episodio, String titulo) {
    FirebaseServices.addParaMasTarde(
        episodio,
        titulo,
        context,
        episodio.getPodcast().getId(),
        episodio.getPodcast().getTitle_original());
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

  public static class ViewHolderEpisodios extends RecyclerView.ViewHolder {
    private ImageView imagenEpisodio, play, recordatorio, masTarde;
    private TextView titulo;

    public ViewHolderEpisodios(@NonNull View itemView) {
      super(itemView);
      imagenEpisodio = itemView.findViewById(R.id.imagenEpisodio);
      play = itemView.findViewById(R.id.reproducir);
      recordatorio = itemView.findViewById(R.id.recordatorio);
      masTarde = itemView.findViewById(R.id.masTarde);
      titulo = itemView.findViewById(R.id.tituloEpisodio);
    }
  }
}
