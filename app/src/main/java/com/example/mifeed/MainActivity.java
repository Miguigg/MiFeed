package com.example.mifeed;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity {
    FirebaseFirestore f;
    ConstraintLayout btnLog,btnReg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        f = FirebaseFirestore.getInstance();
        setContentView(R.layout.activity_main);
        btnLog = findViewById(R.id.toLogin);
        btnReg = findViewById(R.id.toRegistro);
        btnLog.setOnClickListener(view -> {
            gotoLogin();
        });
        btnReg.setOnClickListener(view -> {
            gotoRegistro();
        });
        comprobarSesion();
    }
    protected void onResume() {

        super.onResume();
        comprobarSesion();
    }

    private void comprobarSesion() {
        SharedPreferences sharedpreferences = getSharedPreferences("sesion", Context.MODE_PRIVATE);
        String email = sharedpreferences.getString("email","");
        String pass = sharedpreferences.getString("id","");
        Toast.makeText(MainActivity.this,email,Toast.LENGTH_LONG).show();
        if(email != "" && pass != ""){
            startActivity(new Intent(MainActivity.this,vistageneralActivity.class));
            finish();
        }
    }

    private void gotoRegistro() {
        startActivity(new Intent(MainActivity.this,registroActivity.class));
        finish();
    }

    private void gotoLogin() {
        startActivity(new Intent(MainActivity.this,loginActivity.class));
        finish();
    }
}