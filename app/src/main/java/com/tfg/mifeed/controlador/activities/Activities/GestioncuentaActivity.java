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

  private void borrarCuentaDialog() {
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
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    FirebaseServices.borrarSesionUsuario(user,this.findViewById(android.R.id.content),db,userID);
  }

  private void logout() {
    FirebaseAuth.getInstance().signOut();
    startActivity(new Intent(getApplicationContext(), MainActivity.class));
  }

  public void respuestaBorrado(View v, String res) {
    switch (res) {
      case "true":
        Toast.makeText(v.getContext(), R.string.cuentaBorrada, Toast.LENGTH_LONG).show();
        v.getContext().startActivity(new Intent(v.getContext(),MainActivity.class));
        finish();
        break;
      case "false":
        Toast.makeText(v.getContext(), R.string.errSesion, Toast.LENGTH_LONG).show();
        break;
    }
  }

  public void respuestaDatosUsuario(View v) {
    Toast.makeText(v.getContext(), R.string.errSesion, Toast.LENGTH_SHORT).show();
    FirebaseAuth.getInstance().signOut();
    v.getContext().startActivity(new Intent(v.getContext(), MainActivity.class));
  }

  public void respuestaDatosUsuario(String nombre, String email, String pass, View v) {
    Toast.makeText(v.getContext(), nombre, Toast.LENGTH_SHORT).show();
    EditText editTextNombre, editTextPass, editTextEmail;
    editTextNombre = v.findViewById(R.id.modifNombre);
    editTextPass = v.findViewById(R.id.modifPass);
    editTextEmail = v.findViewById(R.id.modifCorreo);

    editTextEmail.setHint(nombre);
    editTextNombre.setHint(email);
    editTextPass.setHint(pass);
  }
}
