package com.tfg.mifeed.view;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.firebase.firestore.FirebaseFirestore;
import com.tfg.mifeed.R;

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
    }

    protected void onResume() {

        super.onResume();
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