package com.tfg.mifeed.controlador.activities.Activities.Podcast.FragmentsPodcast;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.tfg.mifeed.R;
import com.tfg.mifeed.controlador.firebase.FirebaseServices;
import com.tfg.mifeed.modelo.Recordatorio;

import java.util.ArrayList;

public class RecordatoriosFragment extends Fragment {

  private View v;

  @Override
  public View onCreateView(
      LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    v = inflater.inflate(R.layout.fragment_recordatorios, container, false);
    FirebaseServices.getRecordatorios();
    return inflater.inflate(R.layout.fragment_recordatorios, container, false);
  }

  public void respuestaListaRecordatorios(ArrayList<Recordatorio> recordatorios, String res) {
    switch (res) {
      case "true":
        for (int i = 0; i < recordatorios.size(); i++) {
          Log.d("elemento", recordatorios.get(i).toString());
        }
        break;
    }
  }
}
