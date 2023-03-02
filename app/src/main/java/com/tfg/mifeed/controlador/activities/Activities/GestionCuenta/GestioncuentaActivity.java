package com.tfg.mifeed.controlador.activities.Activities.GestionCuenta;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.tfg.mifeed.R;
import com.tfg.mifeed.controlador.activities.Activities.BienvenidaActivity;
import com.tfg.mifeed.controlador.activities.Activities.Prensa.HistorialActivity;
import com.tfg.mifeed.controlador.activities.Activities.Prensa.PrensaActivity;
import com.tfg.mifeed.controlador.firebase.FirebaseServices;
import com.tfg.mifeed.controlador.utilidades.CheckConexion;
import com.tfg.mifeed.controlador.utilidades.Validaciones;
import com.tfg.mifeed.modelo.Usuario;

public class GestioncuentaActivity extends AppCompatActivity {
  /*
   * Aporta lógica a la vista de edición de cuentas de usuario, esta se conecta a Firebase para
   * realizar los cambios después de las validaciones
   * */
  private ConstraintLayout btnDelete, btnModificaDatos, btnLogout,btnAtras, btnHistorial;
  @SuppressLint("UseSwitchCompatOrMaterialCode")
  private Switch notificaciones, guardadoNube;
  public static View v;
  private EditText nombre, pass, pass2, correo;
  private TextView errUsuario,errEmail,errPass;

  @SuppressLint("MissingInflatedId")
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_gestioncuenta);

    btnLogout = findViewById(R.id.btnlogout);
    btnAtras = findViewById(R.id.btnAtras);
    nombre = findViewById(R.id.modifNombre);
    pass = findViewById(R.id.modifPass2);
    pass2 = findViewById(R.id.modifPass);
    correo = findViewById(R.id.modifCorreo);
    btnDelete = findViewById(R.id.btnBorrarCuenta);
    notificaciones = findViewById(R.id.switchNotificaciones);
    guardadoNube = findViewById(R.id.switchGuardadoNube);
    errUsuario = findViewById(R.id.errEditUsuario);
    errEmail = findViewById(R.id.errEditEmail);
    errPass = findViewById(R.id.errEditPass);
    btnHistorial = findViewById(R.id.btnHistorial);

    btnModificaDatos = findViewById(R.id.btnModificarDatos);
    v = this.findViewById(android.R.id.content);


    // Solicida al serivicio de conexion con Firebase los datos del usuario actual para mostrarlo
    FirebaseServices.getInfoUsuario(v);

    btnLogout.setOnClickListener(
        v -> {
          logout();
        });

    btnDelete.setOnClickListener(
        v -> {
          if(!CheckConexion.getEstadoActual(GestioncuentaActivity.this)){
            Toast.makeText(GestioncuentaActivity.this,R.string.errConn,Toast.LENGTH_LONG).show();
          }else{
            borrarCuentaDialog();
          }
        });
    btnModificaDatos.setOnClickListener(
        v -> {
          if(!CheckConexion.getEstadoActual(GestioncuentaActivity.this)){
            Toast.makeText(GestioncuentaActivity.this,R.string.errConn,Toast.LENGTH_LONG).show();
          }else{
            comprobarDatos();
          }
        });

    btnHistorial.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        if(!CheckConexion.getEstadoActual(GestioncuentaActivity.this)){
          Toast.makeText(GestioncuentaActivity.this,R.string.errConn,Toast.LENGTH_LONG).show();
        }else{
          startActivity(new Intent(getApplicationContext(), HistorialActivity.class));
        }
      }
    });
    btnAtras.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        startActivity(new Intent(getApplicationContext(), PrensaActivity.class));
        finish();
      }
    });
  }

  private void comprobarDatos() {

    String valorNombre = nombre.getText().toString();
    String valorContrasenha1 = pass.getText().toString();
    String valorContrasenha2 = pass2.getText().toString();
    String valorCorreo = correo.getText().toString();
    boolean notificacionesActivas = notificaciones.isChecked();
    boolean guardadoNubeActivo = guardadoNube.isChecked();


    boolean isValid = true;
    if(!correo.getText().toString().isEmpty()){
      if (Validaciones.validacionEmail(valorCorreo).equals("falso")) {
        isValid = false;
        errEmail.setText(R.string.errEmailNoValido);
        errEmail.setVisibility(View.VISIBLE);
      }else{
        errEmail.setVisibility(View.GONE);
      }
    }


    if (!pass.getText().toString().isEmpty()) {
      Log.d("datos",pass.getText().toString());
      if (Validaciones.validacionContraseña(valorContrasenha1, valorContrasenha2).equals("noSegura")) {
        isValid = false;
        errPass.setText(R.string.errContraseñaDebil);
        errPass.setVisibility(View.VISIBLE);
      } else if (Validaciones.validacionContraseña(valorContrasenha1, valorContrasenha2).equals("distintas")) {
        isValid = false;
        errPass.setText(R.string.errContraseñaNoCoincide);
        errPass.setVisibility(View.VISIBLE);
      }
    }else{
      errPass.setVisibility(View.GONE);
      valorContrasenha1 = "";
    }

    if (Validaciones.validacionUser(valorNombre).equals("vacio")) {
      isValid = false;
      errUsuario.setText(R.string.errNombreUsuario);
      errUsuario.setVisibility(View.VISIBLE);
    } else if (Validaciones.validacionUser(valorNombre).equals("noValido")) {
      isValid = false;
      errUsuario.setText(R.string.errNombreUsuarioNoValido);
      errUsuario.setVisibility(View.VISIBLE);
    }else{
      errUsuario.setVisibility(View.GONE);
    }

    if (isValid) {
      Usuario nuevosDatos =
          new Usuario(
              valorNombre,
              valorCorreo,
              valorContrasenha1,
                  notificacionesActivas,
                  guardadoNubeActivo);
      confirmarContrasenhaEdicion(nuevosDatos);
    }
  }

  private void borrarCuentaDialog() {
    /*
     * Muestra un dialogo para avisar al usuario de que está apunto de eliminar todos sus datos
     * y le pide confirmacion
     * */
    AlertDialog.Builder builder = new AlertDialog.Builder(GestioncuentaActivity.this);
    builder.setTitle(R.string.cerrarApp);
    builder
        .setPositiveButton(
            R.string.btnBorrarCuenta,
            new DialogInterface.OnClickListener() {
              @Override
              public void onClick(DialogInterface dialog, int which) {
                confirmarContrasenhaBorrado();
              }
            })
        .setNegativeButton(R.string.aceptarCierre, null);
    builder.show();
  }

  private void confirmarContrasenhaEdicion(Usuario usuario) {
    AlertDialog.Builder builder = new AlertDialog.Builder(this);
    builder.setTitle(R.string.confirmaCont);
    final EditText input = new EditText(this);

    input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
    builder.setView(input);

    builder.setPositiveButton(
            "OK",
            new DialogInterface.OnClickListener() {
              @Override
              public void onClick(DialogInterface dialog, int which) {
                FirebaseServices.comprobarPass(usuario, input.getText().toString());
              }
            });

    builder.setNegativeButton(
            "Cancel",
            new DialogInterface.OnClickListener() {
              @Override
              public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
              }
            });
    builder.show();
  }

  private void confirmarContrasenhaBorrado() {
    /*Antes de eliminar al usuario se confirma, mediante un dialogo, que este es de verdad el
    * propietario de la cuenta pidiendo la contraseña*/

    AlertDialog.Builder builder = new AlertDialog.Builder(this);
    builder.setTitle(R.string.confirmaCont);
    final EditText input = new EditText(this);

    input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
    builder.setView(input);

    builder.setPositiveButton(
        "OK",
        new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int which) {
            FirebaseServices.comprobarPass(input.getText().toString());
          }
        });

    builder.setNegativeButton(
        "Cancel",
        new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int which) {
            dialog.cancel();
          }
        });
    builder.show();
  }

  private void logout() {
    /*
     * Aporta funcionalidad al boton de cerrar sesión de la aplicación.
     * */
    FirebaseAuth.getInstance().signOut();
    SharedPreferences preferences = getSharedPreferences("sesion", 0);
    preferences.edit().remove("email").apply();
    preferences.edit().remove("pass").apply();
    startActivity(new Intent(getApplicationContext(), BienvenidaActivity.class));

  }

  public void respuestaDatosUsuario(View v) {
    /*En caso de que no se haya podido obtener los datos por un problema en la sesion,
     * se cierra esta y se redirige a la bienvenida*/
    Toast.makeText(v.getContext(), R.string.errSesion, Toast.LENGTH_SHORT).show();
    FirebaseAuth.getInstance().signOut();
    v.getContext().startActivity(new Intent(v.getContext(), BienvenidaActivity.class));
  }

  public void respuestaDatosUsuario(String nombre, String email,String notificaciones,String guardarEtiquetas ,View v) {
    /*En caso de que la respuesta sea correcta, se envian los datos y esta funcion se encarga de mostrarlos*/
    EditText editTextNombre, editTextEmail;
    @SuppressLint("UseSwitchCompatOrMaterialCode") Switch switchNotificaciones,switchNube;
    editTextNombre = v.findViewById(R.id.modifNombre);
    editTextEmail = v.findViewById(R.id.modifCorreo);
    switchNotificaciones = v.findViewById(R.id.switchNotificaciones);
    switchNube = v.findViewById(R.id.switchGuardadoNube);

    if(notificaciones.equals("true")){
      switchNotificaciones.setChecked(true);
    }else{
      switchNotificaciones.setChecked(false);
    }
    if(guardarEtiquetas.equals("true")){
      switchNube.setChecked(true);
    }else{
      switchNube.setChecked(false);
    }
    editTextEmail.setText(email);
    editTextNombre.setText(nombre);
  }

  public void respuestaBorrado(String res) {
    /*
     * recibe como parametro la vista actual para poder mostrar el comentario temporal "toast" y el
     * string generado en FirebaseServices en función de la respuesta del servidor
     * */

    switch (res) {
        // Si ha ido bien, redirige a la pestaña de bienvenida y finaliza esta
      case "true":
        FirebaseAuth.getInstance().signOut();
        Toast.makeText(v.getContext(), R.string.cuentaBorrada, Toast.LENGTH_LONG).show();
        v.getContext().startActivity(new Intent(v.getContext(), BienvenidaActivity.class));
        finish();
        break;
        /*Si falla algo, muestra un error y cierra automaticamente la sesion del usuario porque hay
         * un problema con ella*/
      case "false":
        Toast.makeText(v.getContext(), R.string.errSesion, Toast.LENGTH_LONG).show();
        FirebaseAuth.getInstance().signOut();
        finishAndRemoveTask();
        v.getContext().startActivity(new Intent(v.getContext(), BienvenidaActivity.class));

        break;
    }
  }

  public void respuestaTestPass(Usuario usuario,String passAnterior,String res) {
    switch (res) {
      case "true":
        FirebaseServices.editarUsuario(usuario, passAnterior ,v);
        break;
      case "false":
        Toast.makeText(v.getContext(), R.string.errContraseña, Toast.LENGTH_LONG).show();
    }
  }
  public void onBackPressed() {
    startActivity(new Intent(getApplicationContext(), PrensaActivity.class));
    finish();
  }
}
