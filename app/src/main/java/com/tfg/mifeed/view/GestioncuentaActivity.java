package com.tfg.mifeed.view;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.tfg.mifeed.R;

public class GestioncuentaActivity extends AppCompatActivity {
    private ConstraintLayout btnLogout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vistageneral);
        btnLogout = findViewById(R.id.btnlogout);

        btnLogout.setOnClickListener(v -> {
            logout();
        });
    }

    private void logout() {
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(GestioncuentaActivity.this,MainActivity.class));
    }
}