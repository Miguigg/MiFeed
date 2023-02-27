package com.tfg.mifeed.controlador.activities.Activities;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.view.View;
import android.widget.LinearLayout;

import com.tfg.mifeed.R;
import com.tfg.mifeed.controlador.activities.Activities.GestionCuenta.GestioncuentaActivity;
import com.tfg.mifeed.controlador.activities.Activities.Podcast.PodcastMainActivity;
import com.tfg.mifeed.controlador.activities.Activities.Prensa.PrensaActivity;

public class AppActivity extends Application {
  /*
  * Clase que sustituye a la clase Application por defecto y que será accesible por el código de toda la aplicación.
  * En ella se han introducido aquellas funciones relacionadas con la generación de la funcionalidad de las barras inferior y superior
  *
  * */
  @Override
  public void onCreate() {
    super.onCreate();
  }

  public void generarBarraInferior(View v, Activity activity){
    LinearLayout toPrensa = v.findViewById(R.id.toPrensa);
    LinearLayout toPodcast = v.findViewById(R.id.toPodcast);
    LinearLayout toAjustes = v.findViewById(R.id.toAjustes);

    toPrensa.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        v.getContext().startActivity(new Intent(v.getContext(), PrensaActivity.class));
        activity.finish();
      }
    });

    toAjustes.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        v.getContext().startActivity(new Intent(v.getContext(), GestioncuentaActivity.class));
        activity.finish();
      }
    });

    toPodcast.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        v.getContext().startActivity(new Intent(v.getContext(), PodcastMainActivity.class));
        activity.finish();
      }
    });
  }
}
