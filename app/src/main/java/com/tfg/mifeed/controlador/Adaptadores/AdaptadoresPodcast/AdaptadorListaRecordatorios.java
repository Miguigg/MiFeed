package com.tfg.mifeed.controlador.Adaptadores.AdaptadoresPodcast;

import static androidx.core.content.ContextCompat.getSystemService;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
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
import com.tfg.mifeed.controlador.Alarmas.AlarmReceiver;
import com.tfg.mifeed.controlador.activities.Activities.Podcast.DetallesPodcastActivity;
import com.tfg.mifeed.controlador.firebase.FirebasePodcast;
import com.tfg.mifeed.controlador.firebase.FirebaseServices;
import com.tfg.mifeed.controlador.utilidades.CheckConexion;
import com.tfg.mifeed.controlador.utilidades.DescargarEpisodio;
import com.tfg.mifeed.modelo.Episodio;
import com.tfg.mifeed.modelo.Recordatorio;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

public class AdaptadorListaRecordatorios
    extends RecyclerView.Adapter<AdaptadorListaRecordatorios.ViewHolderListaRecordatorios> {

  private final ArrayList<Recordatorio> listaRecordatorios;
  private Context c;
  private MediaPlayer mediaPlayer;
  public FirebasePodcast firebasePodcast;

  private boolean repoduciendo;

  public AdaptadorListaRecordatorios(ArrayList<Recordatorio> listaRecordatorios, Context c) {
    this.listaRecordatorios = listaRecordatorios;
    this.c = c;
    this.repoduciendo = false;
    this.firebasePodcast = new FirebasePodcast();
  }

  @NonNull
  @Override
  public AdaptadorListaRecordatorios.ViewHolderListaRecordatorios onCreateViewHolder(
      @NonNull ViewGroup parent, int viewType) {
    Context context = parent.getContext();
    LayoutInflater inflater = LayoutInflater.from(context);
    View view = inflater.inflate(R.layout.elemento_lista_recordatorios, parent, false);
    ViewHolderListaRecordatorios viewHolderListaRecordatorios =
        new ViewHolderListaRecordatorios(view);
    return viewHolderListaRecordatorios;
  }

  @SuppressLint("StringFormatMatches")
  @Override
  public void onBindViewHolder(
      @NonNull AdaptadorListaRecordatorios.ViewHolderListaRecordatorios holder, int position) {
    ImageView logo = holder.logo;
    TextView titulo = holder.titulo;
    ImageView play = holder.play;
    ImageView masTarde = holder.masTarde;
    ImageView eliminar = holder.eliminar;
    ImageView btnDes = holder.btnDes;
    TextView hora = holder.hora;

    Glide.with(c)
        .load(listaRecordatorios.get(position).getUrlImagen())
        .override(320, 220)
        .into(logo);
    titulo.setText(listaRecordatorios.get(position).getTitulo());
    String fecha = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(listaRecordatorios.get(position).getTimestamp());
    String time =  new SimpleDateFormat("HH:mm", Locale.getDefault()).format(listaRecordatorios.get(position).getTimestamp());
    hora.setText(c.getString(R.string.añadido,fecha,time));

    btnDes.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        if (!CheckConexion.getEstadoActual(c)) {
          Toast.makeText(c, R.string.errConn, Toast.LENGTH_LONG).show();
        } else {
          DescargarEpisodio descargarEpisodio = new DescargarEpisodio(listaRecordatorios.get(holder.getAdapterPosition()).getUrlAudio(),listaRecordatorios.get(holder.getAdapterPosition()).getTitulo(),c);
          descargarEpisodio.accionDescarga();
        }
      }
    });

    play.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View view) {
            if (!CheckConexion.getEstadoActual(c)) {
              Toast.makeText(c, R.string.errConn, Toast.LENGTH_LONG).show();
            } else {
              if (!repoduciendo) {
                repoduciendo = true;
                play.setImageResource(R.drawable.ic_parar_episodio);
                reproducir(listaRecordatorios.get(holder.getAdapterPosition()).getUrlAudio());
              } else {
                repoduciendo = false;
                play.setImageResource(R.drawable.ic_rep_episodio);
                parar();
              }
            }
          }
        });

    logo.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View view) {
            if (!CheckConexion.getEstadoActual(c)) {
              Toast.makeText(c, R.string.errConn, Toast.LENGTH_LONG).show();
            } else {
              Intent intent = new Intent(c, DetallesPodcastActivity.class);
              intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
              intent.putExtra(
                  "urlImagen", listaRecordatorios.get(holder.getAdapterPosition()).getUrlImagen());
              intent.putExtra(
                  "descripcion", listaRecordatorios.get(holder.getAdapterPosition()).getTitulo());
              intent.putExtra(
                  "idPodcast", listaRecordatorios.get(holder.getAdapterPosition()).getIdPodcast());
              intent.putExtra(
                  "titulo", listaRecordatorios.get(holder.getAdapterPosition()).getTitulo());
              c.startActivity(intent);
            }
          }
        });

    masTarde.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View view) {
            Episodio episodio =
                new Episodio(
                    listaRecordatorios.get(holder.getAdapterPosition()).getTitulo(),
                    listaRecordatorios.get(holder.getAdapterPosition()).getUrlImagen(),
                    listaRecordatorios.get(holder.getAdapterPosition()).getUrlAudio());
            redireccionAMasTarde(episodio, episodio.getTitle(),listaRecordatorios.get(holder.getAdapterPosition()).getIdPodcast());
          }
        });

    eliminar.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        if (!CheckConexion.getEstadoActual(c)) {
          Toast.makeText(c, R.string.errConn, Toast.LENGTH_LONG).show();
        } else {
          eliminarAlarmaAndroid(listaRecordatorios.get(holder.getAdapterPosition()).getIdRecordatorio());
          FirebasePodcast.eliminarRecordatorio(listaRecordatorios.get(holder.getAdapterPosition()).getIdPodcast(),c);
          listaRecordatorios.remove(holder.getAdapterPosition());
          notifyItemRemoved(holder.getAdapterPosition());
          notifyItemRangeChanged(holder.getAdapterPosition(),listaRecordatorios.size());
        }
      }
    });

  }
  private void eliminarAlarmaAndroid(int id){
    AlarmManager alarmManager = (AlarmManager) c.getSystemService(Context.ALARM_SERVICE);
    Intent myIntent = new Intent(c, AlarmReceiver.class);
    PendingIntent pendingIntent = PendingIntent.getBroadcast(
            c, id, myIntent,
            PendingIntent.FLAG_IMMUTABLE);
    alarmManager.cancel(pendingIntent);
  }
  private  void  redireccionAMasTarde(Episodio episodio, String titulo, String id){
    FirebasePodcast.addParaMasTarde(episodio,titulo,c,id,titulo);
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
    return listaRecordatorios.size();
  }

  public static class ViewHolderListaRecordatorios extends RecyclerView.ViewHolder {
    ImageView logo, play, masTarde, eliminar, btnDes;
    TextView titulo, hora;

    public ViewHolderListaRecordatorios(@NonNull View itemView) {
      super(itemView);
      logo = itemView.findViewById(R.id.imgPodcast);
      titulo = itemView.findViewById(R.id.tituloRecordatorio);
      play = itemView.findViewById(R.id.btnReproduccion);
      masTarde = itemView.findViewById(R.id.btnMasTarde);
      hora = itemView.findViewById(R.id.horaRecordatorio);
      eliminar = itemView.findViewById(R.id.eliminarRecordatorio);
      btnDes = itemView.findViewById(R.id.btnDes3);
    }
  }
}
