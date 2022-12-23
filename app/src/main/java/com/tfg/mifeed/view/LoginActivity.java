package com.tfg.mifeed.view;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.tfg.mifeed.R;
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
      mAuth.signInWithEmailAndPassword(email,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
        @Override
        public void onComplete(@NonNull Task<AuthResult> task) {
          if(task.isSuccessful()){
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if(user.isEmailVerified()){
              startActivity(new Intent(LoginActivity.this, GestioncuentaActivity.class));
              finish();
              errPass.setVisibility(View.GONE);
            }else{
              if(!emailIsSent){
                user.sendEmailVerification();
                Toast.makeText(LoginActivity.this,R.string.confirmacionCorreo,Toast.LENGTH_LONG).show();
                emailIsSent = true;
              }else{
                AlertDialog.Builder b = new AlertDialog.Builder(LoginActivity.this);
                AlertDialog alert = b.create();
                b.setMessage(R.string.alertCorreo);
                b.setPositiveButton(R.string.volverMandar, new DialogInterface.OnClickListener() {
                  @Override
                  public void onClick(DialogInterface dialog, int which) {
                    user.sendEmailVerification();
                    Toast.makeText(LoginActivity.this,R.string.confirmacionCorreo,Toast.LENGTH_LONG).show();
                  }
                });
                b.setNegativeButton(R.string.entendido, new DialogInterface.OnClickListener() {
                  @Override
                  public void onClick(DialogInterface dialog, int which) {
                    alert.cancel();
                  }
                });
                b.show();
              }
            }
          }else{
            errPass.setText(R.string.errLogin);
            errPass.setVisibility(View.VISIBLE);
          }
        }
      });
    }
  }


  public void onBackPressed() {
    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
    startActivity(intent);
    finish();
  }
}