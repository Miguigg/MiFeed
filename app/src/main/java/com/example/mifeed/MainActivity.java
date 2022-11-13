package com.example.mifeed;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    FirebaseFirestore f;
    Map<String,Object> user = new HashMap<>();
    ConstraintLayout btnLog;
    ConstraintLayout btnReg;
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


        user.put("nomre","paco");
        user.put("Correo","aaaaaaaa");
        user.put("contraseña","aaaaa");
        f.collection("Users").add(user);
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