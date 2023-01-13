package com.tfg.mifeed.controlador.activities.Activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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

public class LoginActivity extends AppCompatActivity {
  private EditText correo,contraseña;
  private ConstraintLayout loginApp, toReg, toReset;
  private TextView errEmail,errPass;
  private Validaciones validaciones = new Validaciones();
  boolean emailIsSent;
  FirebaseServices conexion;
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_login);

    //comprobamos  que el usuario tenga conexion en este momento
    if(!CheckConexion.getEstadoActual(LoginActivity.this)){
      Toast.makeText(LoginActivity.this,R.string.errConn,Toast.LENGTH_LONG).show();
    }else{
      Toast.makeText(LoginActivity.this,R.string.CorrectConn,Toast.LENGTH_LONG).show();
    }

    correo = findViewById(R.id.editTextCorreo);
    contraseña = findViewById(R.id.editTextTextPass);
    errEmail = findViewById(R.id.errLoginEmail);
    errPass = findViewById(R.id.errLoginPass);
    loginApp = findViewById(R.id.ejecutarInicio);
    toReg = findViewById(R.id.toRegistro);
    toReset = findViewById(R.id.toReset);
    emailIsSent = false;
    conexion = new FirebaseServices();

    loginApp.setOnClickListener(view -> {
      iniciarSesion();
    });

    toReset.setOnClickListener(v -> {
      startActivity(new Intent(LoginActivity.this, ResetContrasenha.class));
      finish();
    });

    toReg.setOnClickListener(
        view -> {
          startActivity(new Intent(LoginActivity.this, RegistroActivity.class));
          finish();
        });
  }

  private void iniciarSesion() {
    /*
    * obtiene los datos introducido por el usuario y utiliza las funciones de la clase Validaciones
    * para comprobar que sean validos, si es así, llama a la funciones correspondiente a la login
    * ejecutarLogin()
    * */
    if(!CheckConexion.getEstadoActual(LoginActivity.this)){
      Toast.makeText(LoginActivity.this,R.string.errConn,Toast.LENGTH_LONG).show();
    }else{
      String email = correo.getText().toString().trim();
      String pass = contraseña.getText().toString().trim();


      boolean esValido = true;

      if(validaciones.validacionEmail(email) == "vacio"){
        errEmail.setText(R.string.errEmailVacio);
        esValido = false;
        errEmail.setVisibility(View.VISIBLE);
      }else if(validaciones.validacionEmail(email) == "falso"){
        errEmail.setText(R.string.errEmailNoValido);
        esValido = false;
        errEmail.setVisibility(View.VISIBLE);
      }else{
        errEmail.setVisibility(View.GONE);
      }

      if(validaciones.validacionContraseña(pass) == "vacia"){
        errPass.setText(R.string.errContraseñaVacia);
        esValido = false;
        errPass.setVisibility(View.VISIBLE);
      }else if(validaciones.validacionContraseña(pass) == "noSegura"){
        errPass.setText(R.string.errContraseñaVacia);
        esValido = false;
        errPass.setVisibility(View.VISIBLE);
      }else{
        errPass.setVisibility(View.GONE);
      }

      if(esValido){
        //llama a la funcion de login de FirebaseServices
        FirebaseServices.ejecutarLogin(emailIsSent,email,pass,this.findViewById(android.R.id.content));
        this.emailIsSent = true;
      }
    }
  }

  public void respuestaLogin(String res, View v){
    /*
    * Función accedida por FirebaseServices y que comprueba si fue un login exitoso y actua en consecuencia
    * */
    TextView errorContra = v.findViewById(R.id.errLoginPass);
    switch (res){
      //Si los datos son correctos y el email esta verificado, ejecuta el login
      case "emailVerificado":
        FirebaseServices.comprobarLogin(v.findViewById(android.R.id.content));
        errorContra.setVisibility(View.GONE);
        break;
      //Si el email no esta verificado y MiFeed aun no se lo ha mandado, se lo envia y le avisa
      case "emailNoEnviado":
        Toast.makeText(v.getContext(),R.string.confirmacionCorreo,Toast.LENGTH_LONG).show();
        break;
      case "emailYaEnviado":
        //Si el email ya ha sido enviado, le pregunta si quiere que se le mande otro
        AlertDialog.Builder b = new AlertDialog.Builder(v.getContext());
        AlertDialog alert = b.create();
        b.setMessage(R.string.alertCorreo);
        b.setPositiveButton(R.string.volverMandar, new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int which) {
            FirebaseServices.mandarEmailVerificacion();
            errorContra.setVisibility(View.GONE);
          }
        });
        b.setNegativeButton(R.string.entendido, new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int which) {
            alert.cancel();
            errorContra.setVisibility(View.GONE);
          }
        });
        b.show();
      case "loginFallido":
        //Si algo falla durante el proceso, avisa de que ha ocurrido un error
        errorContra.setText(R.string.errLogin);
        errorContra.setVisibility(View.VISIBLE);
    }
  }

  public void accionLogin(View v, String res, String exito) {
    /*
    * Una vez ejecutado el login, se comprueba si es la primera vez que el usuario inicia sesion y,
    * por tanto, se le debe mostrar la sucesion de pestañas para que elija sus temas favoritos y sus
    * medios de comunicacion preferidos
    * */
    switch (exito){
      case "true":
        Log.d("res",res);
        v.getContext().startActivity(new Intent(v.getContext(), GestioncuentaActivity.class));
        finish();
        break;
      case "false":
        Toast.makeText(v.getContext(),R.string.errLogin,Toast.LENGTH_LONG).show();
        break;
    }
  }


  public void onBackPressed() {
    Intent intent = new Intent(LoginActivity.this, BienvenidaActivity.class);
    startActivity(intent);
    finish();
  }

  public View getCurrentView(){
    return this.getCurrentView();
  }
}
