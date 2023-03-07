package com.tfg.mifeed.controlador.Adaptadores.AdaptadoresPrensa;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.tfg.mifeed.R;
import com.tfg.mifeed.controlador.activities.Activities.GestionCuenta.SeleccionMediosActivity;
import com.tfg.mifeed.modelo.MediosModel;

import org.checkerframework.checker.units.qual.C;

import java.util.ArrayList;

public class AdaptadorListaMedios
    extends RecyclerView.Adapter<AdaptadorListaMedios.ViewHolderListaMedios> {

  private static ArrayList<MediosModel> medios;
  private Context c;

  public AdaptadorListaMedios(ArrayList<MediosModel> medios, Context c) {
    this.medios = medios;
    this.c = c;
  }

  @NonNull
  @Override
  public ViewHolderListaMedios onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    Context context = parent.getContext();
    LayoutInflater inflater = LayoutInflater.from(context);

    View vista = inflater.inflate(R.layout.elemento_lista_medios, parent, false);
    ViewHolderListaMedios viewHolderListaMedios = new ViewHolderListaMedios(vista);
    return viewHolderListaMedios;
  }

  @Override
  public void onBindViewHolder(@NonNull ViewHolderListaMedios holder, int position) {
    CheckBox checkBox = holder.chMedio;
    ImageView imageView = holder.imageView;
    TextView nombre = holder.nombre;
    TextView dominio = holder.dominio;
    imageView.setImageResource(R.drawable.ic_periodico_grande);
    nombre.setText(medios.get(position).getMedio());
    dominio.setText(medios.get(position).getDominio());
    checkBox.setChecked(medios.get(position).isSelected());
  }

  @Override
  public int getItemCount() {
    return medios.size();
  }

  public class ViewHolderListaMedios extends RecyclerView.ViewHolder {
    private ImageView imageView;
    private TextView nombre, dominio;
    private LinearLayout itemLista;

    private final CheckBox chMedio;

    public ViewHolderListaMedios(@NonNull View itemView) {
      super(itemView);
      SeleccionMediosActivity seleccionMediosActivity = new SeleccionMediosActivity();

      imageView = itemView.findViewById(R.id.imgMedio);
      nombre = itemView.findViewById(R.id.nombreMedio);
      dominio = itemView.findViewById(R.id.dominioMedio);
      chMedio = itemView.findViewById(R.id.chMedio);
      itemLista = itemView.findViewById(R.id.itemLista);


      chMedio.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
          boolean isChecked = ((CheckBox) view).isChecked();
          if(isChecked){
            medios.get(getAdapterPosition()).setSelected(true);
            seleccionMediosActivity.insertMedio(
                    medios.get(getAdapterPosition()).getDominio(), medios.get(getAdapterPosition()).getMedio());
          }else{
            medios.get(getAdapterPosition()).setSelected(false);
            seleccionMediosActivity.deleteMedio(
                    medios.get(getAdapterPosition()).getDominio(), medios.get(getAdapterPosition()).getMedio());
          }
        notifyDataSetChanged();
        }
      });
    }
  }
}
