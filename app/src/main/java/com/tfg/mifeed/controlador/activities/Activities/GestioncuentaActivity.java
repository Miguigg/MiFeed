package com.tfg.mifeed.controlador.activities.Activities;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.tfg.mifeed.R;
import com.tfg.mifeed.controlador.firebase.FirebaseServices;
import com.tfg.mifeed.controlador.utilidades.Validaciones;

public class GestioncuentaActivity extends AppCompatActivity {
  /*
   * Aporta lógica a la vista de edición de cuentas de usuario, esta se conecta a Firebase para
   * realizar los cambios después de las validaciones
   * */
  private Validaciones validaciones = new Validaciones();
  private FirebaseUser fUser;
  private ConstraintLayout btnDelete, btnEditPodcast, btnEditMedios, btnModificaDatos, btnLogout;

  @SuppressLint("UseSwitchCompatOrMaterialCode")
  private Switch notificaciones, guardadoNube;
  private View actualView;
  private EditText nombre, pass, pass2, correo;
  private String userID;
  private boolean notificacionesActivas, guardadoNubeActivo;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_gestioncuenta);
    btnLogout = findViewById(R.id.btnlogout);
    nombre = findViewById(R.id.modifNombre);
    pass = findViewById(R.id.modifPass2);
    pass2 = findViewById(R.id.modifPass);
    correo = findViewById(R.id.modifCorreo);
    btnDelete = findViewById(R.id.btnBorrarCuenta);
    btnEditPodcast = findViewById(R.id.btnEditPodcast);
    btnEditMedios = findViewById(R.id.btnEditPrensa);
    notificaciones = findViewById(R.id.switchNotificaciones);
    guardadoNube = findViewById(R.id.switchGuardadoNube);

    btnModificaDatos = findViewById(R.id.btnModificarDatos);
    actualView = this.findViewById(android.R.id.content);
    fUser = FirebaseAuth.getInstance().getCurrentUser();
    userID = fUser.getUid();

    // Solicida al serivicio de conexion con Firebase los datos del usuario actual para mostrarlo
    FirebaseServices.getInfoUsuario(actualView);

    btnLogout.setOnClickListener(
        v -> {
          logout();
        });

    btnDelete.setOnClickListener(
        v -> {
          borrarCuentaDialog();
        });
    btnModificaDatos.setOnClickListener(
        v -> {
          comprobarDatos();
        });
  }

  private void comprobarDatos() {

    String valorNombre = nombre.getText().toString();
    String valorContrasenha1 = pass.getText().toString();
    String valorContrasenha2 = pass2.getText().toString();
    String valorCorreo = correo.getText().toString();
    notificacionesActivas = notificaciones.isChecked();
    guardadoNubeActivo = guardadoNube.isChecked();

    boolean isValid = true;

    if (validaciones.validacionEmail(valorCorreo) == "vacio") {
      isValid = false;
      correo.setHint(R.string.errEmailVacio);
    } else if (validaciones.validacionEmail(valorCorreo) == "noValido") {
      isValid = false;
      correo.setHint(R.string.errEmailNoValido);
    } else {
      correo.setHint("");
    }

    if (validaciones.validacionContraseña(valorContrasenha1, valorContrasenha2) == "vacia") {
      isValid = false;
      pass.setHint(R.string.errContraseñaVacia);
    } else if (validaciones.validacionContraseña(valorContrasenha1, valorContrasenha2)
        == "noSegura") {
      isValid = false;
      pass.setHint(R.string.errContraseñaDebil);
    } else if (validaciones.validacionContraseña(valorContrasenha1, valorContrasenha2)
        == "distintas") {
      isValid = false;
      pass.setHint(R.string.errContraseñaNoCoincide);
    } else {
      pass.setHint("");
    }

    if (validaciones.validacionUser(valorNombre) == "vacio") {
      isValid = false;
      nombre.setHint(R.string.errNombreUsuario);
    } else if (validaciones.validacionUser(valorNombre) == "falso") {
      isValid = false;
      nombre.setHint(R.string.errNombreUsuarioNoValido);
    } else {
      pass.setHint("");
    }

    if (isValid) {
      confirmarContrasenhaEdicion(valorNombre,valorContrasenha1,valorCorreo);//todo pasar los valores actuales por parametro
    }
  }

  public void respuestaDatosUsuario(View v) {
    /*En caso de que no se haya podido obtener los datos por un problema en la sesion,
     * se cierra esta y se redirige a la bienvenida*/
    Toast.makeText(v.getContext(), R.string.errSesion, Toast.LENGTH_SHORT).show();
    FirebaseAuth.getInstance().signOut();
    v.getContext().startActivity(new Intent(v.getContext(), BienvenidaActivity.class));
  }

  public void respuestaDatosUsuario(String nombre, String email, String pass, View v) {
    /*En caso de que la respuesta sea correcta, se envian los datos y esta funcion se encarga de mostrarlos*/
    Toast.makeText(v.getContext(), nombre, Toast.LENGTH_SHORT).show();
    EditText editTextNombre, editTextPass, editTextEmail;
    editTextNombre = v.findViewById(R.id.modifNombre);
    editTextEmail = v.findViewById(R.id.modifCorreo);

    editTextEmail.setText(email);
    editTextNombre.setText(nombre);
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

  private void confirmarContrasenhaBorrado(){
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
                FirebaseServices.comprobarPass(actualView, input.getText().toString());
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


  private void confirmarContrasenhaEdicion(String valorNombre, String valorPass, String valorCorreo) {
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
            FirebaseServices.comprobarPass(valorNombre,valorPass,valorCorreo,actualView, input.getText().toString());
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

  private void borrarCuenta() {
    /*
     * Una vez el usuario acepta, obtenemos la instancia actual de firebase y el usuario actual,
     * luego se procede a llamar a la funcion de borrado de cuenta que está en
     * com.tfg.mifeed.controlador.firebase.FirebaseServices
     * */
    FirebaseServices.borrarSesionUsuario(actualView);
  }

  private void logout() {
    /*
     * Aporta funcionalidad al boton de cerrar sesión de la aplicación.
     * */
    FirebaseAuth.getInstance().signOut();
    startActivity(new Intent(getApplicationContext(), BienvenidaActivity.class));
  }

  public void respuestaBorrado(View v, String res) {
    /*
     * recibe como parametro la vista actual para poder mostrar el comentario temporal "toast" y el
     * string generado en FirebaseServices en función de la respuesta del servidor
     * */

    switch (res) {
        // Si ha ido bien, redirige a la pestaña de bienvenida y finaliza esta
      case "true":
        Toast.makeText(v.getContext(), R.string.cuentaBorrada, Toast.LENGTH_LONG).show();
        v.getContext().startActivity(new Intent(v.getContext(), BienvenidaActivity.class));
        finish();
        break;
        /*Si falla algo, muestra un error y cierra automaticamente la sesion del usuario porque hay
         * un problema con ella*/
      case "false":
        Toast.makeText(v.getContext(), R.string.errSesion, Toast.LENGTH_LONG).show();
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(getApplicationContext(), BienvenidaActivity.class));
        break;
    }
  }

  public void respuestaTestPass(String valorNombre, String valorPass,String valorCorreo,View v, String res) {
    switch (res) {
      case "true":
        Toast.makeText(v.getContext(), valorNombre, Toast.LENGTH_SHORT).show();
        break;
      case "false":
        Toast.makeText(v.getContext(), R.string.errConn, Toast.LENGTH_LONG).show();
    }
  }
}
