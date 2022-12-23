package com.tfg.mifeed.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.tfg.mifeed.R;
import com.tfg.mifeed.controlador.utilidades.Validaciones;

public class ResetContrasenha extends AppCompatActivity {
    private Validaciones validaciones = new Validaciones();
    private EditText correo;
    private ConstraintLayout btnReseteo;
    FirebaseAuth auth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        auth = FirebaseAuth.getInstance();
        setContentView(R.layout.activity_reset_contrasenha);
        correo = findViewById(R.id.correoRenovacion);
        btnReseteo = findViewById(R.id.btnReseteo);
        btnReseteo.setOnClickListener(v -> {
            reseteo();
        });
    }

    private void reseteo() {
        String email = correo.getText().toString().trim();
        TextView errReset = findViewById(R.id.errReset);
        boolean isvalid = true;
        if(validaciones.validacionEmail(email) == "vacio"){
            errReset.setText(R.string.errEmailVacio);
            isvalid = false;
            errReset.setVisibility(View.VISIBLE);
        }else if(validaciones.validacionEmail(email) == "falso"){
            errReset.setText(R.string.errEmailNoValido);
            isvalid = false;
            errReset.setVisibility(View.VISIBLE);
        }else{
            errReset.setVisibility(View.GONE);
        }
        if(isvalid){
      auth.sendPasswordResetEmail(email)
          .addOnCompleteListener(
              new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                  if (task.isSuccessful()) {
                    Toast.makeText(ResetContrasenha.this, R.string.confirmacioCambio,
                            Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(ResetContrasenha.this, LoginActivity.class));
                  } else {
                    errReset.setText(R.string.errEmailNoValido);
                    errReset.setVisibility(View.VISIBLE);
                  }
                }
              });
        }
    }

}