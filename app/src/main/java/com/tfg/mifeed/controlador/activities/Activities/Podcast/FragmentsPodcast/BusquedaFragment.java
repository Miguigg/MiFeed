package com.tfg.mifeed.controlador.activities.Activities.Podcast.FragmentsPodcast;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.tfg.mifeed.R;
import com.tfg.mifeed.controlador.adaptadores.AdaptadorListaEpisodios;
import com.tfg.mifeed.controlador.conexioPodcastApi.ApiPodcastConn;
import com.tfg.mifeed.modelo.Episodio;
import com.tfg.mifeed.modelo.RespuestaListaEpisodios;

import org.checkerframework.checker.units.qual.C;

import java.util.ArrayList;
import java.util.Calendar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BusquedaFragment extends Fragment {

  private EditText valorBusqqueda;
  private TextView errBusqueda;
  private ImageView btnBuscar;
  private View v;
  private RecyclerView listaEpisodios;
  private ProgressBar carga;
  private static Context context;
  private Spinner idioma;

  @Override
  public View onCreateView(
      LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    v = inflater.inflate(R.layout.fragment_busqueda, container, false);
    valorBusqqueda = v.findViewById(R.id.valorBusqueda);
    btnBuscar = v.findViewById(R.id.btnBusqueda);
    listaEpisodios = v.findViewById(R.id.listaEpisodios);
    errBusqueda = v.findViewById(R.id.errBusqueda);
    carga = v.findViewById(R.id.cargaBusqueda);
    idioma = v.findViewById(R.id.idioma);
    context = getContext().getApplicationContext();
    String[] idiomas = {"Español", "Inglés", "Gallego", "Francés", "Italiano", "Portugués"};
    ArrayAdapter<String> adaptador =
        new ArrayAdapter<>(v.getContext(), android.R.layout.simple_spinner_item, idiomas);
    idioma.setAdapter(adaptador);
    btnBuscar.setOnClickListener(
        v -> {
          if (!TextUtils.isEmpty(valorBusqqueda.getText().toString())) {
            Log.d("idioma", (String) idioma.getSelectedItem());
            carga.setVisibility(View.VISIBLE);
            String valiorIntroducido = valorBusqqueda.getText().toString();
            String toret = "";
            switch ((String) idioma.getSelectedItem()) {
              case "Español":
                toret = "Spanish";
                break;
              case "Inglés":
                toret = "English";
                break;
              case "Gallego":
                toret = "Galician";
                break;
              case "Francés":
                toret = "French";
                break;
              case "Italiano":
                toret = "Italian";
                break;
              case "Portugués":
                toret = "Portuguese";
                break;
              default:
                toret = "Spanish";
                break;
            }
            getEpisodios(valiorIntroducido, toret);
          }
        });
    return v;
  }

  private void getEpisodios(String valor, String idioma) {
    ApiPodcastConn.getApiConnInterfacePodcast()
        .getEpisodiosConBusqueda(valor, 1, "episode", "20", idioma)
        .enqueue(
            new Callback<RespuestaListaEpisodios>() {
              @Override
              public void onResponse(
                  Call<RespuestaListaEpisodios> call, Response<RespuestaListaEpisodios> response) {
                ArrayList<Episodio> listaCapitulos = new ArrayList<>();
                listaCapitulos.addAll(response.body().getResults());
                if (listaCapitulos.size() > 0) {
                  AdaptadorListaEpisodios adaptadorListaEpisodios =
                      new AdaptadorListaEpisodios(
                          listaCapitulos, context);
                  LinearLayoutManager linearLayoutManager =
                      new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
                  listaEpisodios.setLayoutManager(linearLayoutManager);
                  listaEpisodios.setAdapter(adaptadorListaEpisodios);

                  carga.setVisibility(View.GONE);
                  errBusqueda.setVisibility(View.GONE);
                } else {
                  carga.setVisibility(View.GONE);
                  errBusqueda.setVisibility(View.VISIBLE);
                }
              }

              @Override
              public void onFailure(Call<RespuestaListaEpisodios> call, Throwable t) {
                carga.setVisibility(View.GONE);
                errBusqueda.setVisibility(View.VISIBLE);
              }
            });
  }
}
