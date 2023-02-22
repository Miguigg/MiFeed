package com.tfg.mifeed.controlador.utilidades;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Patterns;

import androidx.appcompat.app.AlertDialog;

import com.tfg.mifeed.R;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
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
    Pattern formato = Pattern.compile("(^$|^[A-Za-z0-9\\s()=\\-+áéíóúñÁÉÍÓÚÑ]{1,15}$)");
    if (nombreUsuario.isEmpty()) {
      return "vacio";
    } else if (!formato.matcher(nombreUsuario).matches()) {
      return "noValido";
    } else {
      return "ok";
    }
  }

  public static String validacionContraseña(String contraseñaUsuario1, String contraseñaUsuario2) {
    Pattern formato = Pattern.compile("^[a-zA-Z0-9áéíóúÁÉÍÓÚ]{4,40}$");
    if (contraseñaUsuario1.isEmpty() || contraseñaUsuario2.isEmpty()) {
      return "vacia";
    } else if (!contraseñaUsuario1.equals(contraseñaUsuario2)) {
      return "distintas";
    } else if (!formato.matcher(contraseñaUsuario1).matches()) {
      return "noSegura";
    } else {
      return "ok";
    }
  }

  public static String validacionContraseña(String contraseñaLogin) {
    /*Para el uso del login*/
    Pattern formato = Pattern.compile("^[a-zA-Z0-9áéíóúÁÉÍÓÚ]{4,40}$");
    if (!formato.matcher(contraseñaLogin).matches()) {
      return "noSegura";
    } else {
      return "ok";
    }
  }

  public static void confirmarExit(Context c) {
    new AlertDialog.Builder(c)
        .setMessage(R.string.cerrarApp)
        .setCancelable(false)
        .setPositiveButton(
            "Seguro",
            new DialogInterface.OnClickListener() {
              @Override
              public void onClick(DialogInterface dialog, int which) {
                ((Activity) (c)).finish();
              }
            })
        .setNegativeButton("No", null)
        .show();
  }

  public static String hashearMD5(String password) {
    /*Dada la contraseña introducida por el usuario, se calcula el hash md5 para guardarla en su
     *coleccion de firebase*/
    try {
      MessageDigest md = MessageDigest.getInstance("MD5");

      byte[] messageDigest = md.digest(password.getBytes());

      BigInteger no = new BigInteger(1, messageDigest);

      StringBuilder hashtext = new StringBuilder(no.toString(16));
      while (hashtext.length() < 32) {
        hashtext.insert(0, "0");
      }
      return hashtext.toString();

    } catch (NoSuchAlgorithmException e) {
      throw new RuntimeException(e);
    }
  }
  static final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
  public static Date getDateFromString(String fechaString){

    try {
      Date fechaFormateada = format.parse(fechaString);
      return fechaFormateada ;
    } catch (ParseException e){
      return null ;
    }

  }
}
