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
import com.tfg.mifeed.controlador.firebase.FirebaseServices;
import com.tfg.mifeed.controlador.utilidades.CheckConexion;
import com.tfg.mifeed.modelo.Episodio;

import java.io.IOException;
import java.util.ArrayList;

public class AdaptadorListaEpisodiosPodcast extends RecyclerView.Adapter<AdaptadorListaEpisodiosPodcast.ViewHolderListaEpisodios> {

    ArrayList<Episodio> listaEpisodios;
    String idPodcast, tituloPodcast;
    Context context;
    MediaPlayer mediaPlayer;
    boolean reproduciendo;

    public AdaptadorListaEpisodiosPodcast(ArrayList<Episodio> listaEpisodios, Context context, String idPodcast, String tituloPodcast) {
        this.context = context;
        this.listaEpisodios = listaEpisodios;
        this.reproduciendo = false;
        this.idPodcast = idPodcast;
        this.tituloPodcast = tituloPodcast;
    }

    @NonNull
    @Override
    public ViewHolderListaEpisodios onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.elemento_lista_episodios, parent, false);
        ViewHolderListaEpisodios viewHolderListaEpisodios = new ViewHolderListaEpisodios(view);
        return viewHolderListaEpisodios;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderListaEpisodios holder, int position) {
        TextView titulo = holder.titulo;
        titulo.setText(fromHtml(listaEpisodios.get(position).getTitle(), 0));
        ImageView imageView = holder.imagenEpisodio;
        Glide.with(context)
                .load(listaEpisodios.get(position).getImage())
                .override(400, 300)
                .into(imageView);
        ImageView play = holder.play;
        ImageView recordatorio = holder.recordatorio;
        ImageView masTarde = holder.masTarde;
            play.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (!CheckConexion.getEstadoActual(context)) {
                                Toast.makeText(context, R.string.errConn, Toast.LENGTH_LONG).show();
                            } else {
                                if (!reproduciendo) {
                                    reproduciendo = true;
                                    play.setImageResource(R.drawable.ic_parar_episodio);
                                    reproducir(listaEpisodios.get(holder.getAdapterPosition()).getAudio());
                                } else {
                                    reproduciendo = false;
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
                                                fromHtml(listaEpisodios.get(holder.getAdapterPosition()).getTitle(), 0)));
                            }
                        }
                    });
    recordatorio.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View view) {
            Intent intent = new Intent(context, CreacionRecordatorioActivity.class);
            intent.putExtra(
                "nombreEpisodio", listaEpisodios.get(holder.getAdapterPosition()).getTitle());
            intent.putExtra("urlAudio", listaEpisodios.get(holder.getAdapterPosition()).getAudio());
            intent.putExtra(
                "urlImagen", listaEpisodios.get(holder.getAdapterPosition()).getImage());
            intent.putExtra("idPodcast", idPodcast);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
          }
        });
    }

    private void addIdEpisodio(Episodio episodio, String titulo) {
        FirebaseServices.addParaMasTarde(episodio, titulo, context, idPodcast, tituloPodcast);
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
            reproducir.start();
            //mediaPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    Thread reproducir = new Thread(new Runnable() {
        @Override
        public void run() {
            mediaPlayer.start();
        }
    });


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

    public static class ViewHolderListaEpisodios extends RecyclerView.ViewHolder {
        ImageView imagenEpisodio, pausa, play, recordatorio, masTarde;
        TextView titulo;

        public ViewHolderListaEpisodios(@NonNull View itemView) {
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
