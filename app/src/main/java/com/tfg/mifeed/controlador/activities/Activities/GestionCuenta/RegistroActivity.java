package com.tfg.mifeed.controlador.activities.Activities.GestionCuenta;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.tfg.mifeed.R;
import com.tfg.mifeed.controlador.activities.Activities.BienvenidaActivity;
import com.tfg.mifeed.controlador.firebase.FirebaseServices;
import com.tfg.mifeed.controlador.utilidades.CheckConexion;
import com.tfg.mifeed.controlador.utilidades.Validaciones;
import com.tfg.mifeed.modelo.Usuario;

public class RegistroActivity extends AppCompatActivity implements View.OnClickListener {
  private Validaciones validaciones = new Validaciones();
  private FirebaseServices conexion;
  private EditText nombre, email, contrasenha1, contrasenha2;
  private ConstraintLayout registro, toInicioSesion;


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_registro);

    nombre = findViewById(R.id.nombre);
    email = findViewById(R.id.correo);
    contrasenha1 = findViewById(R.id.contrasenha1);
    contrasenha2 = findViewById(R.id.contrasenha2);
    registro = findViewById(R.id.accionRegistro);
    toInicioSesion = findViewById(R.id.accionInicio);
    registro.setOnClickListener(this);
    toInicioSesion.setOnClickListener(this);
    conexion = new FirebaseServices();
    //final AppActivity app = (AppActivity) this.getApplication();
    //Toast.makeText(RegistroActivity.this,String.valueOf(app.generarBarraInferior()),Toast.LENGTH_LONG).show();
  }

  @Override
  public void onBackPressed() {
    startActivity(new Intent(RegistroActivity.this, BienvenidaActivity.class));
    finish();
  }

  @Override
  public void onClick(View view) {
    switch (view.getId()) {
      case R.id.accionRegistro:
        registroUsuario();
        break;
      case R.id.accionInicio:
        startActivity(new Intent(RegistroActivity.this, LoginActivity.class));
        finish();
        break;
    }
  }

  private void registroUsuario() {
    if(!CheckConexion.getEstadoActual(RegistroActivity.this)){
      Toast.makeText(RegistroActivity.this,R.string.errConn,Toast.LENGTH_LONG).show();
    }else{

      String nombreUsuario = nombre.getText().toString().trim();
      String emailUsuario = email.getText().toString().trim();
      String contrasenhaUsuario1 = contrasenha1.getText().toString().trim();
      String contrasenhaUsuario2 = contrasenha2.getText().toString().trim();

      TextView errUsuario = findViewById(R.id.errUsuario);
      TextView errPass = findViewById(R.id.errPass);
      TextView errEmail = findViewById(R.id.errEmail);

      boolean isvalid = true;


      if(validaciones.validacionUser(nombreUsuario) == "vacio"){
        errUsuario.setText(R.string.errNombreUsuario);
        errUsuario.setVisibility(View.VISIBLE);
        isvalid = false;
      }else if(validaciones.validacionUser(nombreUsuario) == "noValido"){
        errUsuario.setText(R.string.errNombreUsuarioNoValido);
        errUsuario.setVisibility(View.VISIBLE);
        isvalid = false;
      }else{
        errUsuario.setVisibility(View.GONE);
      }

      if(validaciones.validacionEmail(emailUsuario) == "vacio"){
        errEmail.setText(R.string.errEmailVacio);
        isvalid = false;
        errEmail.setVisibility(View.VISIBLE);
      }else if(validaciones.validacionEmail(emailUsuario) == "falso"){
        errEmail.setText(R.string.errEmailNoValido);
        isvalid = false;
        errEmail.setVisibility(View.VISIBLE);
      }else{
        errEmail.setVisibility(View.GONE);
      }

      if (validaciones.validacionContraseña(contrasenhaUsuario1,contrasenhaUsuario2) == "vacia"){
        errPass.setText(R.string.errContraseñaVacia);
        isvalid = false;
        errPass.setVisibility(View.VISIBLE);
      }else if(validaciones.validacionContraseña(contrasenhaUsuario1,contrasenhaUsuario2) == "noSegura"){
        errPass.setText(R.string.errContraseñaDebil);
        isvalid = false;
        errPass.setVisibility(View.VISIBLE);
      }else if(validaciones.validacionContraseña(contrasenhaUsuario1,contrasenhaUsuario2) == "distintas"){
        errPass.setText(R.string.errContraseñaNoCoincide);
        isvalid = false;
        errPass.setVisibility(View.VISIBLE);
      }else{
        errPass.setVisibility(View.GONE);
      }

      if (isvalid) {
        insercionEnFirebase(new Usuario(nombreUsuario, emailUsuario, contrasenhaUsuario1));
      }
    }
  }

  private void insercionEnFirebase(Usuario usuario) {
    FirebaseServices.ejecutarRegistro(usuario,this.findViewById(android.R.id.content));
  }

  public void respuestaRegistro(String res,View v){
    switch (res){
      case "valido":
        Intent intent = new Intent(v.getContext(), BienvenidaActivity.class);
        v.getContext().startActivity(intent);
        Toast.makeText(v.getContext(),R.string.RegCorrecto,Toast.LENGTH_LONG).show();
        finish();
        break;
      case "NoValido":
        Toast.makeText(v.getContext(),R.string.errRegistro,Toast.LENGTH_LONG).show();
        break;
      case "yaExiste":
        Toast.makeText(v.getContext(),R.string.errEmailExistente,Toast.LENGTH_LONG).show();
    }
  }
}
