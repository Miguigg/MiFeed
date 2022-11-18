package com.example.mifeed;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;

public class loginActivity extends AppCompatActivity {
  private ActivityResultLauncher<Intent> activityResultLauncher;
  GoogleSignInOptions gso;
  GoogleSignInClient gsc;
  ConstraintLayout loginApp, toReg;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_login);
    loginApp = findViewById(R.id.ejecutarInicio);
    toReg = findViewById(R.id.toRegistro);

    loginApp.setOnClickListener(view -> {});

    toReg.setOnClickListener(
        view -> {
          startActivity(new Intent(loginActivity.this, registroActivity.class));
          finish();
        });
  }


  public void onBackPressed() {
    Intent intent = new Intent(loginActivity.this, MainActivity.class);
    startActivity(intent);
    finish();
  }
}
