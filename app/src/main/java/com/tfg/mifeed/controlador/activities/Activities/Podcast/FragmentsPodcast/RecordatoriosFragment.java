package com.tfg.mifeed.controlador.activities.Activities.Podcast.FragmentsPodcast;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.tfg.mifeed.R;
import com.tfg.mifeed.controlador.Adaptadores.AdaptadoresPodcast.AdaptadorListaRecordatorios;
import com.tfg.mifeed.controlador.firebase.FirebasePodcast;
import com.tfg.mifeed.controlador.firebase.FirebaseServices;
import com.tfg.mifeed.controlador.utilidades.CheckConexion;
import com.tfg.mifeed.modelo.Recordatorio;

import java.util.ArrayList;

public class RecordatoriosFragment extends Fragment {

  private static View v;
  private static ProgressBar carga;
  private static TextView err;
  private static RecyclerView listaRecordatorios;
  private static ArrayList<Recordatorio> respuestaRecordatorios;
  public FirebasePodcast firebasePodcast;

  @Override
  public View onCreateView(
      LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    v = inflater.inflate(R.layout.fragment_recordatorios, container, false);
    carga = v.findViewById(R.id.cargaRecordatorios);
    err = v.findViewById(R.id.errRecordatorios);
    listaRecordatorios = v.findViewById(R.id.listaRecordatorios);
    firebasePodcast = new FirebasePodcast();
    if(!CheckConexion.getEstadoActual(v.getContext())){
      Toast.makeText(v.getContext(),R.string.errConn,Toast.LENGTH_LONG).show();
      carga.setVisibility(View.GONE);
    }else{
      FirebasePodcast.getRecordatorios(v);
    }
    return v;
  }

  private void rellenarLista(View v){
    /*
    * Inicializa el adaptador que usará para representar los recordatorios
    * */
    LinearLayoutManager linearLayoutManager =
            new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
    AdaptadorListaRecordatorios adaptadorListaRecordatorios = new AdaptadorListaRecordatorios(respuestaRecordatorios, v.getContext());
    listaRecordatorios.setAdapter(adaptadorListaRecordatorios);
    listaRecordatorios.setLayoutManager(linearLayoutManager);
  }

  public void respuestaListaRecordatorios(ArrayList<Recordatorio> recordatorios, String res, View v) {
    /*
    * Una vez ha recibido los recordatorios, los representa mandandoselos al adaptador
    * */
    switch (res) {
      case "true":
        for (int i = 0; i < recordatorios.size(); i++) {
          Log.d("elemento", recordatorios.get(i).toString());
          respuestaRecordatorios = recordatorios;
          err.setVisibility(View.GONE);
          carga.setVisibility(View.GONE);
          rellenarLista(v);
        }
        break;
      case "false":
        Toast.makeText(v.getContext(), R.string.errRecordatorios, Toast.LENGTH_SHORT).show();
        carga.setVisibility(View.GONE);
        err.setVisibility(View.VISIBLE);
    }
  }
}
