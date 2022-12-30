package com.tfg.mifeed.controlador.utilidades;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Patterns;

import androidx.appcompat.app.AlertDialog;

import com.tfg.mifeed.R;

import java.util.regex.Pattern;

public class Validaciones {
  public static String validacionEmail(String emailUsuario) {
    if (emailUsuario.isEmpty()) {
      return "vacio";
    } else if (!Patterns.EMAIL_ADDRESS.matcher(emailUsuario).matches()) {
      return "falso";
    } else {
      return "ok";
    }
  }

  public static String validacionUser(String nombreUsuario) {
    Pattern formato = Pattern.compile("(^$|^[A-Za-z0-9\\s(),.¿?=\\-+áéíóúñÁÉÍÓÚÑ]{1,15}$)");
    if (nombreUsuario.isEmpty()) {
      return "vacio";
    } else if (!formato.matcher(nombreUsuario).matches()) {
      return "noValido";
    } else {
      return "ok";
    }
  }

  public static String validacionContraseña(String contraseñaUsuario1, String contraseñaUsuario2) {
    Pattern formato = Pattern.compile("^[a-zA-Z0-9áéíóúÁÉÍÓÚ]{3,40}$");
    if (contraseñaUsuario1.isEmpty() || contraseñaUsuario2.isEmpty()) {
      return "vacia";
    } else if (!formato.matcher(contraseñaUsuario1).matches()) {
      return "noSegura";
    } else if (!contraseñaUsuario1.equals(contraseñaUsuario2)) {
      return "distintas";
    } else {
      return "ok";
    }
  }

  public static String validacionContraseña(String contraseñaLogin) {
    Pattern formato = Pattern.compile("^[a-zA-Z0-9áéíóúÁÉÍÓÚ]{3,40}$");
    if (!formato.matcher(contraseñaLogin).matches()) {
      return "noSegura";
    } else if (contraseñaLogin.isEmpty()) {
      return "vacia";
    } else {
      return "ok";
    }
  }

  public static void confirmarExit(Context c) {
    new AlertDialog.Builder(c).setMessage(R.string.cerrarApp)
            .setCancelable(false)
            .setPositiveButton("Seguro", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    ((Activity)(c)).finish();
                }
            })
            .setNegativeButton("No",null)
            .show();
  }
}
