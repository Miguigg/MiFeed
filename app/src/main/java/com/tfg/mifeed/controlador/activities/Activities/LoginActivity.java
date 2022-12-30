package com.tfg.mifeed.controlador.activities.Activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
import com.tfg.mifeed.controlador.utilidades.CheckConexion;
import com.tfg.mifeed.controlador.utilidades.Validaciones;

public class LoginActivity extends AppCompatActivity {
  private EditText correo,contraseña;
  private ConstraintLayout loginApp, toReg, toReset;
  TextView errEmail;
  TextView errPass;
  private FirebaseAuth mAuth;
  private Validaciones validaciones = new Validaciones();
  boolean emailIsSent;
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_login);

    if(!CheckConexion.getEstadoActual(LoginActivity.this)){
      Toast.makeText(LoginActivity.this,R.string.errConn,Toast.LENGTH_LONG).show();
    }else{
      Toast.makeText(LoginActivity.this,R.string.CorrectConn,Toast.LENGTH_LONG).show();
    }

    correo = findViewById(R.id.editTextCorreo);
    contraseña = findViewById(R.id.editTextTextPass);
    errEmail = findViewById(R.id.errLoginEmail);
    errPass = findViewById(R.id.errLoginPass);
    loginApp = findViewById(R.id.ejecutarInicio);
    toReg = findViewById(R.id.toRegistro);
    toReset = findViewById(R.id.toReset);
    emailIsSent = false;
    mAuth = FirebaseAuth.getInstance();

    loginApp.setOnClickListener(view -> {
      iniciarSesion();
    });

    toReset.setOnClickListener(v -> {
      startActivity(new Intent(LoginActivity.this, ResetContrasenha.class));
      finish();
    });

    toReg.setOnClickListener(
        view -> {
          startActivity(new Intent(LoginActivity.this, RegistroActivity.class));
          finish();
        });
  }

  private void iniciarSesion() {
    if(!CheckConexion.getEstadoActual(LoginActivity.this)){
      Toast.makeText(LoginActivity.this,R.string.errConn,Toast.LENGTH_LONG).show();
    }else{
      String email = correo.getText().toString().trim();
      String pass = contraseña.getText().toString().trim();


      boolean esValido = true;

      if(validaciones.validacionEmail(email) == "vacio"){
        errEmail.setText(R.string.errEmailVacio);
        esValido = false;
        errEmail.setVisibility(View.VISIBLE);
      }else if(validaciones.validacionEmail(email) == "falso"){
        errEmail.setText(R.string.errEmailNoValido);
        esValido = false;
        errEmail.setVisibility(View.VISIBLE);
      }else{
        errEmail.setVisibility(View.GONE);
      }

      if(validaciones.validacionContraseña(pass) == "vacia"){
        errPass.setText(R.string.errContraseñaVacia);
        esValido = false;
        errPass.setVisibility(View.VISIBLE);
      }else if(validaciones.validacionContraseña(pass) == "noSegura"){
        errPass.setText(R.string.errContraseñaDebil);
        esValido = false;
        errPass.setVisibility(View.VISIBLE);
      }else{
        errPass.setVisibility(View.GONE);
      }

      if(esValido){
        FirebaseServices.ejecutarLogin(mAuth,emailIsSent,email,pass,this.findViewById(android.R.id.content));
        this.emailIsSent = true;
      }
    }
  }

  public void respuestaLogin(String res, View v){
    TextView errorContra = v.findViewById(R.id.errLoginPass);
    switch (res){
      case "emailVerificado":
        FirebaseFirestore db =  FirebaseFirestore.getInstance();
        String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseServices.comprobarLogin(db,userID, v.findViewById(android.R.id.content));
        errorContra.setVisibility(View.GONE);
        break;
      case "emailNoEnviado":
        Toast.makeText(v.getContext(),R.string.confirmacionCorreo,Toast.LENGTH_LONG).show();
        break;
      case "emailYaEnviado":
        AlertDialog.Builder b = new AlertDialog.Builder(v.getContext());
        AlertDialog alert = b.create();
        b.setMessage(R.string.alertCorreo);
        b.setPositiveButton(R.string.volverMandar, new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int which) {
            FirebaseServices.mandarEmailVerificacion(FirebaseAuth.getInstance().getCurrentUser());
            errorContra.setVisibility(View.GONE);
          }
        });
        b.setNegativeButton(R.string.entendido, new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int which) {
            alert.cancel();
            errorContra.setVisibility(View.GONE);
          }
        });
        b.show();
      case "loginFallido":
        errorContra.setText(R.string.errLogin);
        errorContra.setVisibility(View.VISIBLE);
    }
  }

  public void accionLogin(View v, String res, String exito) {
    switch (exito){
      case "true":
        Log.d("res",res);
        v.getContext().startActivity(new Intent(v.getContext(), GestioncuentaActivity.class));
        finish();
        break;
      case "false":
        Toast.makeText(v.getContext(),R.string.errLogin,Toast.LENGTH_LONG).show();
        break;
    }
  }


  public void onBackPressed() {
    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
    startActivity(intent);
    finish();
  }
}
