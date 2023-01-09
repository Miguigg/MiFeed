package com.tfg.mifeed.controlador.activities.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.tfg.mifeed.R;
import com.tfg.mifeed.controlador.firebase.FirebaseServices;
import com.tfg.mifeed.controlador.utilidades.CheckConexion;
import com.tfg.mifeed.controlador.utilidades.Validaciones;

public class ResetContrasenha extends AppCompatActivity {
    private Validaciones validaciones = new Validaciones();
    private EditText correo;
    private ConstraintLayout btnReseteo;
    FirebaseServices conexion;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_contrasenha);
        correo = findViewById(R.id.correoRenovacion);
        btnReseteo = findViewById(R.id.btnReseteo);
        btnReseteo.setOnClickListener(v -> {
            reseteo();
        });
        conexion = new FirebaseServices();
    }

    private void reseteo() {
        /*
        * Se ejecuta cuando el usuario toca el boton de ejecutar el cambio de contraseña, antes de ello
        * se valida el email y se llama a la funcion de FirebaseServices
        * */
        if(!CheckConexion.getEstadoActual(ResetContrasenha.this)){
            Toast.makeText(ResetContrasenha.this,R.string.errConn,Toast.LENGTH_LONG).show();
        }else{
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
                FirebaseServices.resetEmail(email,this.findViewById(android.R.id.content));
            }
        }
    }

    public void validacionOK(boolean correcto, View v){
        TextView errReset = v.findViewById(R.id.errReset);
        if(correcto){
            Toast.makeText(v.getContext(), R.string.confirmacioCambio,
                    Toast.LENGTH_SHORT).show();
            v.getContext().startActivity(new Intent(v.getContext(), LoginActivity.class));
            finish();
        }else{
            errReset.setText(R.string.errEmailNoValido);
            errReset.setVisibility(View.VISIBLE);
        }
    }
}