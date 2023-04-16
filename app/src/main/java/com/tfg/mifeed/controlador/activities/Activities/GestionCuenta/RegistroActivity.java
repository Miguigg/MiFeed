package com.tfg.mifeed.controlador.activities.Activities.GestionCuenta;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.tfg.mifeed.R;
import com.tfg.mifeed.controlador.activities.Activities.BienvenidaActivity;
import com.tfg.mifeed.controlador.firebase.FirebaseGestionUsuario;
import com.tfg.mifeed.controlador.firebase.FirebaseServices;
import com.tfg.mifeed.controlador.utilidades.CheckConexion;
import com.tfg.mifeed.controlador.utilidades.Validaciones;
import com.tfg.mifeed.modelo.Usuario;

public class RegistroActivity extends AppCompatActivity implements View.OnClickListener {
  //private Validaciones validaciones = new Validaciones();
  private FirebaseGestionUsuario conexion;
  public static View v;
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
    v = this.findViewById(android.R.id.content);
    conexion = new FirebaseGestionUsuario();
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
    /*
    * Valida los datos introducidos por el usuario y, si son correctos, ejecuta la funcion de insercion
    * */
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

      if (Validaciones.validacionUser(nombreUsuario).equals("vacio")) {
        errUsuario.setText(R.string.errNombreUsuario);
        errUsuario.setVisibility(View.VISIBLE);
        isvalid = false;
      } else if (Validaciones.validacionUser(nombreUsuario).equals("noValido")) {
        errUsuario.setText(R.string.errNombreUsuarioNoValido);
        errUsuario.setVisibility(View.VISIBLE);
        isvalid = false;
      }else{
        errUsuario.setVisibility(View.GONE);
      }

      if (Validaciones.validacionEmail(emailUsuario).equals("vacio")) {
        errEmail.setText(R.string.errEmailVacio);
        isvalid = false;
        errEmail.setVisibility(View.VISIBLE);
      } else if (Validaciones.validacionEmail(emailUsuario).equals("falso")) {
        errEmail.setText(R.string.errEmailNoValido);
        isvalid = false;
        errEmail.setVisibility(View.VISIBLE);
      }else{
        errEmail.setVisibility(View.GONE);
      }

      if (Validaciones.validacionContraseña(contrasenhaUsuario1, contrasenhaUsuario2).equals("vacia")){
        errPass.setText(R.string.errContraseñaVacia);
        isvalid = false;
        errPass.setVisibility(View.VISIBLE);
      }else if(Validaciones.validacionContraseña(contrasenhaUsuario1, contrasenhaUsuario2).equals("noSegura")){
        errPass.setText(R.string.errContraseñaDebil);
        isvalid = false;
        errPass.setVisibility(View.VISIBLE);
      }else if(Validaciones.validacionContraseña(contrasenhaUsuario1, contrasenhaUsuario2).equals("distintas")){
        errPass.setText(R.string.errContraseñaNoCoincide);
        isvalid = false;
        errPass.setVisibility(View.VISIBLE);
      }else{
        errPass.setVisibility(View.GONE);
      }

      if (isvalid) {
        FirebaseGestionUsuario.ejecutarRegistro(new Usuario(nombreUsuario, emailUsuario, contrasenhaUsuario1));
      }
    }
  }

  public void respuestaRegistro(String res){
    /*
    * Recibe el codigo de respuesta de firebase e informa al usuario del resultado
    * */
    switch (res){
      case "valido":
        FirebaseAuth.getInstance().signOut();
        v.getContext().startActivity(new Intent(v.getContext(), BienvenidaActivity.class));
        Toast.makeText(v.getContext(),R.string.RegCorrecto,Toast.LENGTH_LONG).show();
        break;
      case "NoValido":
        FirebaseAuth.getInstance().signOut();
        v.getContext().startActivity(new Intent(v.getContext(), BienvenidaActivity.class));
        Toast.makeText(v.getContext(),R.string.errRegistro,Toast.LENGTH_LONG).show();
        break;
      case "yaExiste":
        FirebaseAuth.getInstance().signOut();
        v.getContext().startActivity(new Intent(v.getContext(), BienvenidaActivity.class));
        Toast.makeText(v.getContext(),R.string.errEmailExistente,Toast.LENGTH_LONG).show();
    }
  }
}
