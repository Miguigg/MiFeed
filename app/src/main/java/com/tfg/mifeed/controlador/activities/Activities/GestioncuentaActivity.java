package com.tfg.mifeed.controlador.activities.Activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.tfg.mifeed.R;
import com.tfg.mifeed.controlador.firebase.FirebaseServices;

public class GestioncuentaActivity extends AppCompatActivity {
  /*
  * Aporta lógica a la vista de edición de cuentas de usuario, esta se conecta a Firebase para
  * realizar los cambios después de las validaciones
  * */
  private ConstraintLayout btnLogout;
  private FirebaseFirestore db;
  private FirebaseUser fUser;
  private ConstraintLayout btnDelete;

  EditText nombre, pass, correo;
  String userID;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_vistageneral);
    btnLogout = findViewById(R.id.btnlogout);

    nombre = findViewById(R.id.modifNombre);
    pass = findViewById(R.id.modifPass);
    correo = findViewById(R.id.modifCorreo);
    btnDelete = findViewById(R.id.btnBorrarCuenta);

    db = FirebaseFirestore.getInstance();
    fUser = FirebaseAuth.getInstance().getCurrentUser();
    userID = fUser.getUid();

    //Solicida al serivicio de conexion con Firebase los datos del usuario actual para mostrarlo
    FirebaseServices.getInfoUsuario(userID, db, this.findViewById(android.R.id.content));

    btnLogout.setOnClickListener(
        v -> {
          logout();
        });

    btnDelete.setOnClickListener(
        v -> {
          borrarCuentaDialog();
        });
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
    editTextPass = v.findViewById(R.id.modifPass);
    editTextEmail = v.findViewById(R.id.modifCorreo);

    editTextEmail.setHint(nombre);
    editTextNombre.setHint(email);
    editTextPass.setHint(pass);
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
                borrarCuenta();
              }
            })
        .setNegativeButton(R.string.aceptarCierre, null);
    builder.show();
  }

  private void borrarCuenta() {
    /*
    * Una vez el usuario acepta, obtenemos la instancia actual de firebase y el usuario actual,
    * luego se procede a llamar a la funcion de borrado de cuenta que está en
    * com.tfg.mifeed.controlador.firebase.FirebaseServices
    * */
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    FirebaseServices.borrarSesionUsuario(user,this.findViewById(android.R.id.content),db,userID);
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
      //Si ha ido bien, redirige a la pestaña de bienvenida y finaliza esta
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
}
