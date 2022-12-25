package com.tfg.mifeed.view;

import android.content.Context;
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
import com.google.firebase.firestore.FirebaseFirestore;
import com.tfg.mifeed.R;
import com.tfg.mifeed.controlador.firebase.FirebaseServices;
import com.tfg.mifeed.controlador.utilidades.Validaciones;
import com.tfg.mifeed.modelo.Usuario;

import java.util.HashMap;
import java.util.Map;

public class RegistroActivity extends AppCompatActivity implements View.OnClickListener {
  private Validaciones validaciones = new Validaciones();
  private FirebaseAuth mAuth;
  FirebaseFirestore firestore;
  Map<String, Object> user = new HashMap<>();
  final AppActivity app = (AppActivity) this.getApplication();
  private EditText nombre, email, contrasenha1, contrasenha2;
  private ConstraintLayout registro, toInicioSesion;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_registro);
    mAuth = FirebaseAuth.getInstance();

    nombre = findViewById(R.id.nombre);
    email = findViewById(R.id.correo);
    contrasenha1 = findViewById(R.id.contrasenha1);
    contrasenha2 = findViewById(R.id.contrasenha2);
    registro = findViewById(R.id.accionRegistro);
    toInicioSesion = findViewById(R.id.accionInicio);
    registro.setOnClickListener(this);
    toInicioSesion.setOnClickListener(this);
  }

  @Override
  public void onBackPressed() {
    startActivity(new Intent(RegistroActivity.this, MainActivity.class));
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

  private void insercionEnFirebase(Usuario usuario) {
    firestore = FirebaseFirestore.getInstance();
    FirebaseServices.ejecutarRegistro(firestore,mAuth,usuario,this.findViewById(android.R.id.content));
  }

  public void respuestaRegistro(String res,View v){
    switch (res){
      case "valido":
        Intent intent = new Intent(v.getContext(),MainActivity.class);
        v.getContext().startActivity(intent);
        finish();
        break;
      case "NoValido":
        Toast.makeText(v.getContext(),R.string.errRegistro,Toast.LENGTH_LONG).show();
        break;
      case "yaExiste":
        Toast.makeText(v.getContext(),R.string.errEmailExistente,Toast.LENGTH_LONG).show();
    }
  }

  private void setSession(String email, String id) {
    SharedPreferences sharedpreferences = getSharedPreferences("sesion", Context.MODE_PRIVATE);
    SharedPreferences.Editor editor = sharedpreferences.edit();
    editor.putString("email",email);
    editor.putString("id",id);
    Toast.makeText(RegistroActivity.this,email,Toast.LENGTH_LONG).show();
    editor.commit();
    editor.apply();
  }
}
