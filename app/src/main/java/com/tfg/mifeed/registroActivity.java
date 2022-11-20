package com.tfg.mifeed;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.tfg.mifeed.R;
import com.tfg.mifeed.core.Usuario;
import com.tfg.mifeed.core.validaciones;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class registroActivity extends AppCompatActivity implements View.OnClickListener {
  private validaciones validaciones = new validaciones();
  private FirebaseAuth mAuth;
  FirebaseFirestore f;
  Map<String, Object> user = new HashMap<>();
  final appActivity app = (appActivity) this.getApplication();
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
    startActivity(new Intent(registroActivity.this, MainActivity.class));
    finish();
  }

  @Override
  public void onClick(View view) {
    switch (view.getId()) {
      case R.id.accionRegistro:
        registroUsuario();
        break;
      case R.id.accionInicio:
        startActivity(new Intent(registroActivity.this, loginActivity.class));
        finish();
        break;
    }
  }

  private void registroUsuario() {

    Pattern p = Pattern.compile("^[a-zA-Z0-9áéíóúÁÉÍÓÚ]{3,40}$");

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

    /*if (nombreUsuario.isEmpty()) {
      errUsuario.setVisibility(View.VISIBLE);
      isvalid = false;
    } else {
      errUsuario.setVisibility(View.GONE);
    }*/

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
   /* if (emailUsuario.isEmpty()) {
      errEmail.setText(R.string.errEmailVacio);
      isvalid = false;
      errEmail.setVisibility(View.VISIBLE);
    } else if (!Patterns.EMAIL_ADDRESS.matcher(emailUsuario).matches()) {
      errEmail.setText(R.string.errEmailNoValido);
      isvalid = false;
      errEmail.setVisibility(View.VISIBLE);
    } else {
      errEmail.setVisibility(View.GONE);
    }*/

    if (contrasenhaUsuario1.isEmpty() || contrasenhaUsuario2.isEmpty()) {
      errPass.setText(R.string.errContraseñaVacia);
      isvalid = false;
      errPass.setVisibility(View.VISIBLE);
    } else if (!p.matcher(contrasenhaUsuario1).matches()) {
      errPass.setText(R.string.errContraseñaDebil);
      isvalid = false;
      errPass.setVisibility(View.VISIBLE);
    } else if (!contrasenhaUsuario1.equals(contrasenhaUsuario2)) {
      errPass.setText(R.string.errContraseñaNoCoincide);
      isvalid = false;
      errPass.setVisibility(View.VISIBLE);
    } else {
      errPass.setVisibility(View.GONE);
    }
    if (isvalid) {
      insercionEnFirebase(new Usuario(nombreUsuario, emailUsuario, contrasenhaUsuario1));
    }
  }

  private void insercionEnFirebase(Usuario usuario) {
    f = FirebaseFirestore.getInstance();
    mAuth
        .createUserWithEmailAndPassword(usuario.getEmail(), usuario.getContraseña())
        .addOnCompleteListener(
            new OnCompleteListener<AuthResult>() {
              @Override
              public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {

                  user.put("id", mAuth.getCurrentUser().getUid());
                  user.put("nombre", usuario.getNombre());
                  user.put("correo", usuario.getEmail());
                  user.put("contraseña", usuario.getContraseña());
                  f.collection("Users")
                      .add(user)
                      .addOnCompleteListener(
                          new OnCompleteListener<DocumentReference>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentReference> task) {
                              if (task.isSuccessful()) {
                                Toast.makeText(
                                        registroActivity.this,
                                        "Registro Exitoso",
                                        Toast.LENGTH_LONG)
                                    .show();
                              }else{
                                Toast.makeText(
                                                registroActivity.this,
                                                "Registro fallido",
                                                Toast.LENGTH_LONG)
                                        .show();
                              }
                            }
                          });
                }else{
                  Toast.makeText(registroActivity.this,String.valueOf(task.getException()),Toast.LENGTH_LONG).show();
                }
              }
            });
    setSession(usuario.getEmail(),mAuth.getCurrentUser().getUid());
  }

  private void setSession(String email, String id) {
    SharedPreferences sharedpreferences = getSharedPreferences("sesion", Context.MODE_PRIVATE);
    SharedPreferences.Editor editor = sharedpreferences.edit();
    editor.putString("email",email);
    editor.putString("id",id);
    Toast.makeText(registroActivity.this,email,Toast.LENGTH_LONG).show();
    editor.commit();
    editor.apply();
  }
}
