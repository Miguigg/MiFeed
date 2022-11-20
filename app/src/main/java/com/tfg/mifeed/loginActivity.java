package com.tfg.mifeed;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;

import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.tfg.mifeed.R;
import com.google.firebase.auth.FirebaseAuth;

public class loginActivity extends AppCompatActivity {
  private ActivityResultLauncher<Intent> activityResultLauncher;
  private EditText correo,contraseña;
  private ConstraintLayout loginApp, toReg;
  private FirebaseAuth mAuth;
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_login);
    correo = findViewById(R.id.editTextCorreo);
    contraseña = findViewById(R.id.editTextTextPass);

    loginApp = findViewById(R.id.ejecutarInicio);
    toReg = findViewById(R.id.toRegistro);
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
  }


  public void onBackPressed() {
    Intent intent = new Intent(loginActivity.this, MainActivity.class);
    startActivity(intent);
    finish();
  }
}
