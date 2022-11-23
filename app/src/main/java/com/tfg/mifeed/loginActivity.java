package com.tfg.mifeed;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.tfg.mifeed.core.validaciones;

public class loginActivity extends AppCompatActivity {
  private ActivityResultLauncher<Intent> activityResultLauncher;
  private EditText correo,contraseña;
  private ConstraintLayout loginApp, toReg;
  TextView errEmail;
  TextView errPass;
  private FirebaseAuth mAuth;
  private com.tfg.mifeed.core.validaciones validaciones = new validaciones();
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
    emailIsSent = false;
    mAuth = FirebaseAuth.getInstance();
    loginApp.setOnClickListener(view -> {
      iniciarSesion();
    });

    toReg.setOnClickListener(
        view -> {
          startActivity(new Intent(loginActivity.this, registroActivity.class));
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
              startActivity(new Intent(loginActivity.this, vistageneralActivity.class));
              finish();
              errPass.setVisibility(View.GONE);
            }else{
              if(!emailIsSent){
                user.sendEmailVerification();
                Toast.makeText(loginActivity.this,R.string.confirmacionCorreo,Toast.LENGTH_LONG).show();
                emailIsSent = true;
              }else{
                AlertDialog.Builder b = new AlertDialog.Builder(loginActivity.this);
                AlertDialog alert = b.create();
                b.setMessage(R.string.alertCorreo);
                b.setPositiveButton(R.string.volverMandar, new DialogInterface.OnClickListener() {
                  @Override
                  public void onClick(DialogInterface dialog, int which) {
                    user.sendEmailVerification();
                    Toast.makeText(loginActivity.this,R.string.confirmacionCorreo,Toast.LENGTH_LONG).show();
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
    Intent intent = new Intent(loginActivity.this, MainActivity.class);
    startActivity(intent);
    finish();
  }
}
