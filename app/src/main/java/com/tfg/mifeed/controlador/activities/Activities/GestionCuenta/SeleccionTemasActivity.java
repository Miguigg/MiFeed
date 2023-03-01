package com.tfg.mifeed.controlador.activities.Activities.GestionCuenta;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.tfg.mifeed.R;
import com.tfg.mifeed.controlador.activities.Activities.BienvenidaActivity;
import com.tfg.mifeed.controlador.activities.Activities.Prensa.PrensaActivity;
import com.tfg.mifeed.controlador.firebase.FirebaseServices;
import com.tfg.mifeed.controlador.utilidades.CheckConexion;

import java.util.ArrayList;

public class SeleccionTemasActivity extends AppCompatActivity {
  private Switch deportes, negocios, ocio, ciencia, salud, tecnologia;
  private ConstraintLayout btnAceptar;
  public static View v;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_seleccion_temas);
    deportes = findViewById(R.id.switchDeporte);
    negocios = findViewById(R.id.switchNegocios);
    ocio = findViewById(R.id.switchOcio);
    ciencia = findViewById(R.id.switchCiencia);
    salud = findViewById(R.id.switchSalud);
    tecnologia = findViewById(R.id.switchTecnologia);
    btnAceptar = findViewById(R.id.btnAceptar);
    v = this.findViewById(android.R.id.content);
    btnAceptar.setOnClickListener(v -> {
      if(!CheckConexion.getEstadoActual(SeleccionTemasActivity.this)){
        Toast.makeText(SeleccionTemasActivity.this,R.string.errConn,Toast.LENGTH_LONG).show();
      }else{
        checkSelección();
      }
    });
  }

  private void checkSelección() {
    boolean isDeportes = deportes.isChecked();
    boolean isNegocios = negocios.isChecked();
    boolean isOcio = ocio.isChecked();
    boolean isCiencia = ciencia.isChecked();
    boolean isSalud = salud.isChecked();
    boolean isTecnologia = tecnologia.isChecked();
    boolean algoSelecionado = false;
    if (isDeportes || isNegocios || isOcio || isCiencia || isSalud || isTecnologia) {
      algoSelecionado = true;
    }
    if (!algoSelecionado) {
      Toast.makeText(this, R.string.errSeleccionTemas, Toast.LENGTH_SHORT).show();
    }else{
      ArrayList<String> toret = new ArrayList<>();
      if(isDeportes){
        toret.add("sports");
      }
      if(isNegocios){
        toret.add("business");
      }
      if(isOcio){
        toret.add("entertainment");
      }
      if(isCiencia){
        toret.add("science");
      }
      if(isSalud){
        toret.add("health");
      }
      if(isTecnologia){
        toret.add("technology");
      }
      FirebaseServices.setTemasUsuario(toret);
    }
  }

  public void respuestaSetTemas(String res){
    switch (res){
      case "true":
        v.getContext().startActivity(new Intent(v.getContext(), SeleccionMediosActivity.class));
        finish();
        break;
      case "false":
        FirebaseAuth.getInstance().signOut();
        v.getContext().startActivity(new Intent(v.getContext(), BienvenidaActivity.class));
        finish();
        break;
    }
  }
  public void onBackPressed() {
    Intent intent = new Intent(SeleccionTemasActivity.this, PrensaActivity.class);
    startActivity(intent);
    finish();
  }
}
