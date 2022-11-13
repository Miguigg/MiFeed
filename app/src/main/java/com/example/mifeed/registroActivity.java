package com.example.mifeed;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.EditText;

public class registroActivity extends AppCompatActivity {
  final appActivity app = (appActivity) this.getApplication();
  private EditText nombre, email ,contrasenha1,contrasenha2;
  private ConstraintLayout registro,toInicioSesion;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_registro);
    nombre = findViewById(R.id.nombre);
    email = findViewById(R.id.correo);
    contrasenha1 = findViewById(R.id.contrasenha1);
    contrasenha2 = findViewById(R.id.contrasenha2);

  }
}