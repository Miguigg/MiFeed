package com.tfg.mifeed.controlador.activities.Activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.firebase.firestore.FirebaseFirestore;
import com.tfg.mifeed.R;
import com.tfg.mifeed.controlador.utilidades.Validaciones;

public class BienvenidaActivity extends AppCompatActivity {

    /*
    *Clase que aporta funcionalidad a la vista de bienvenida y se mostrará al acceder a la aplicación
    */

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

    @Override
    public void onBackPressed() {
        Validaciones.confirmarExit(BienvenidaActivity.this);
    }

    private void gotoRegistro() {
        /*Apora al boton de registro la capacidad de redirigir a la penstaña de
        *registro y finalizar esta actividad
        * para evitar ciclos en las actividades*/
        startActivity(new Intent(BienvenidaActivity.this, RegistroActivity.class));
        finish();
    }

    private void gotoLogin() {
        /*Apora al boton de registro la capacidad de redirigir a la penstaña de
         *login y finalizar esta actividad
         * para evitar ciclos en las actividades*/
        startActivity(new Intent(BienvenidaActivity.this, LoginActivity.class));
        finish();
    }
}