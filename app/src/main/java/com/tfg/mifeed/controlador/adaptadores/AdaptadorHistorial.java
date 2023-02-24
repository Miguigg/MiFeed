package com.tfg.mifeed.controlador.adaptadores;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.tfg.mifeed.R;
import com.tfg.mifeed.controlador.activities.Activities.Prensa.NoticiaActivity;
import com.tfg.mifeed.controlador.firebase.FirebaseServices;
import com.tfg.mifeed.controlador.utilidades.CheckConexion;

import java.util.ArrayList;

public class AdaptadorHistorial extends RecyclerView.Adapter<AdaptadorHistorial.ViewHolderHistorial> {

  private ArrayList<String> listaUrls;
  private Context context;

  public AdaptadorHistorial(ArrayList<String> lista, Context c) {
    this.listaUrls = lista;
    this.context = c;
  }

  @NonNull
  @Override
  public ViewHolderHistorial onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    Context context = parent.getContext();
    LayoutInflater inflater = LayoutInflater.from(context);
    View vista = inflater.inflate(R.layout.elemento_lista_historial, parent, false);
    ViewHolderHistorial viewHolderHistorial = new ViewHolderHistorial(vista);
    return viewHolderHistorial;
  }

  @Override
  public void onBindViewHolder(@NonNull ViewHolderHistorial holder, int position) {
    TextView url = holder.url;
    ImageView btnEliminar = holder.eliminar;
    url.setText(listaUrls.get(position));

    url.setOnClickListener(
        v -> {
          if (!CheckConexion.getEstadoActual(context)) {
            Toast.makeText(context, R.string.errConn, Toast.LENGTH_LONG).show();
          } else {
            toWeb(listaUrls.get(position));
          }
        });

    btnEliminar.setOnClickListener(
        v -> {
          if (!CheckConexion.getEstadoActual(context)) {
            Toast.makeText(context, R.string.errConn, Toast.LENGTH_LONG).show();
          } else {
            eliminarHistorial(position);
          }
        });
  }

  public void eliminarHistorial(int pos) {
    FirebaseServices.eliminarElementoHistorial(listaUrls.get(pos));
    listaUrls.remove(pos);
    notifyItemRemoved(pos);
    notifyItemRangeChanged(pos, listaUrls.size());
  }

  public void toWeb(String url) {
    Intent intent = new Intent(context, NoticiaActivity.class);
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    intent.putExtra("enlace", url);
    context.startActivity(intent);
  }

  @Override
  public int getItemCount() {
    return listaUrls.size();
  }

  public static class ViewHolderHistorial extends RecyclerView.ViewHolder {

    TextView url;
    ImageView eliminar;

    public ViewHolderHistorial(@NonNull View itemView) {
      super(itemView);

      url = itemView.findViewById(R.id.url);
      eliminar = itemView.findViewById(R.id.eliminarElemento);
    }
  }
}
