package com.tfg.mifeed.controlador.firebase;

import static com.tfg.mifeed.controlador.utilidades.Validaciones.hashearMD5;

import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.tfg.mifeed.R;
import com.tfg.mifeed.controlador.activities.Activities.BienvenidaActivity;
import com.tfg.mifeed.controlador.activities.Activities.GestioncuentaActivity;
import com.tfg.mifeed.controlador.activities.Activities.LoginActivity;
import com.tfg.mifeed.controlador.activities.Activities.RegistroActivity;
import com.tfg.mifeed.controlador.activities.Activities.ResetContrasenha;
import com.tfg.mifeed.modelo.Usuario;

import java.util.HashMap;
import java.util.Map;

public class FirebaseServices {

  private static FirebaseFirestore instancia;
  private static FirebaseAuth userAuth;

  public FirebaseServices() {
    this.instancia = FirebaseFirestore.getInstance();
    this.userAuth = FirebaseAuth.getInstance();
  }

  public static void ejecutarLogin(boolean emailSent, String email, String pass, View v) {
    Log.d("enviado", String.valueOf(emailSent));
    userAuth
        .signInWithEmailAndPassword(email, pass)
        .addOnCompleteListener(
            new OnCompleteListener<AuthResult>() {
              @Override
              public void onComplete(@NonNull Task<AuthResult> task) {
                LoginActivity login = new LoginActivity();
                if (task.isSuccessful()) {
                  if (FirebaseAuth.getInstance().getCurrentUser().isEmailVerified()) {
                    login.respuestaLogin("emailVerificado", v);
                  } else {
                    if (!emailSent) {
                      FirebaseServices.mandarEmailVerificacion();
                      login.respuestaLogin("emailNoEnviado", v);
                    } else {
                      login.respuestaLogin("emailYaEnviado", v);
                    }
                  }
                } else {
                  login.respuestaLogin("loginFallido", v);
                }
              }
            });
  }

  public static void ejecutarRegistro(Usuario usuario, View v) {
    RegistroActivity registroActivity = new RegistroActivity();
    userAuth
        .createUserWithEmailAndPassword(usuario.getEmail(), usuario.getContraseña())
        .addOnCompleteListener(
            new OnCompleteListener<AuthResult>() {
              @Override
              public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                  Map<String, Object> user = new HashMap<>();
                  user.put("id", userAuth.getCurrentUser().getUid());
                  user.put("nombre", usuario.getNombre());
                  user.put("correo", usuario.getEmail());
                  user.put(
                      "contraseña",
                      hashearMD5(usuario.getContraseña())); // insertamos la contraseña encriptada
                  user.put("firstLogin", "true");
                  user.put("notificaciones", "true");
                  user.put("guardarEtiquetas", "true");
                  instancia
                      .collection("Users")
                      .document(userAuth.getCurrentUser().getUid())
                      .set(user)
                      .addOnCompleteListener(
                          new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                              if (task.isSuccessful()) {
                                registroActivity.respuestaRegistro("valido", v);
                              } else {
                                registroActivity.respuestaRegistro("NoValido", v);
                              }
                            }
                          });
                } else {
                  try {
                    throw task.getException();
                  } catch (FirebaseAuthUserCollisionException e) {
                    registroActivity.respuestaRegistro("yaExiste", v);
                  } catch (Exception e) {
                    Log.e("TAG", e.getMessage());
                  }
                }
              }
            });
  }

  public static void mandarEmailVerificacion() {
    userAuth.getCurrentUser().sendEmailVerification();
  }

  public static void resetEmail(String email, View v) {
    ResetContrasenha rst = new ResetContrasenha();
    userAuth
        .sendPasswordResetEmail(email)
        .addOnCompleteListener(
            new OnCompleteListener<Void>() {
              @Override
              public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                  rst.validacionOK(task.isSuccessful(), v);
                }
              }
            });
  }

  public static void getInfoUsuario(View v) {

    GestioncuentaActivity ges = new GestioncuentaActivity();
    String id = userAuth.getCurrentUser().getUid();
    DocumentReference ref = instancia.collection("Users").document(id);
    ref.get()
        .addOnSuccessListener(
            new OnSuccessListener<DocumentSnapshot>() {
              @Override
              public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                  String nombre = documentSnapshot.getString("nombre");
                  String correo = documentSnapshot.getString("correo");
                  String notificaciones = documentSnapshot.getString("notificaciones");
                  String nube = documentSnapshot.getString("guardarEtiquetas");
                  ges.respuestaDatosUsuario(nombre,correo,notificaciones,nube, v);

                } else {

                  ges.respuestaDatosUsuario(v);
                }
              }
            })
        .addOnFailureListener(
            new OnFailureListener() {
              @Override
              public void onFailure(@NonNull Exception e) {
                Log.d("e", String.valueOf(e));
                ges.respuestaDatosUsuario(v);
              }
            });
  }

  public static void editarUsuario(Usuario usuario, String passAnterior, View v) {

    if (usuario.getContraseña().equals("")) {
      usuario.setContraseña(passAnterior);
    }

    userAuth
        .getCurrentUser()
        .updatePassword(usuario.getContraseña())
        .addOnCompleteListener(
            new OnCompleteListener<Void>() {
              @Override
              public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(v.getContext(), R.string.modificacionPass, Toast.LENGTH_SHORT).show();
                  Log.d("exito", "contraseña modificada");
                } else {
                  Toast.makeText(v.getContext(), R.string.errModifPass, Toast.LENGTH_SHORT).show();
                }
              }
            });

    String id = userAuth.getCurrentUser().getUid();

    DocumentReference ref = instancia.collection("Users").document(id);
    Map<String, Object> user = new HashMap<>();
    user.put("nombre", usuario.getNombre());
    user.put("correo", usuario.getEmail());
    user.put("contraseña", hashearMD5(usuario.getContraseña()));
    user.put("firstLogin", "false");
    if (usuario.isNotificaciones()) {
      user.put("notificaciones", "true");
    } else {
      user.put("notificaciones", "false");
    }
    if (usuario.isEtiquetasNube()) {
      user.put("guardarEtiquetas", "true");
    } else {
      user.put("guardarEtiquetas", "false");
    }

    ref.set(user)
        .addOnCompleteListener(
            new OnCompleteListener<Void>() {
              @Override
              public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Log.d("exito", "Modificación exitosa");
                } else {
                    Log.e("exito", "Modificación erronea");
                }
              }
            });

    if (!usuario.getEmail().equals(userAuth.getCurrentUser().getEmail())) {
      userAuth
          .getCurrentUser()
          .reauthenticate(
              EmailAuthProvider.getCredential(
                  userAuth.getCurrentUser().getEmail(), passAnterior))
          .addOnCompleteListener(
              new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                  if (task.isSuccessful()) {
                      Toast.makeText(v.getContext(), R.string.modificacionCorreo, Toast.LENGTH_SHORT).show();
                    Log.d("exito", "Modificación exitosa");
                  } else {
                      Toast.makeText(v.getContext(), R.string.errModificacionEmail , Toast.LENGTH_SHORT).show();
                    Log.e("fracaso", "Error de sesion");
                  }

                  userAuth
                      .getCurrentUser()
                      .updateEmail(usuario.getEmail())
                      .addOnCompleteListener(
                          new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                              if (task.isSuccessful()) {
                                FirebaseAuth.getInstance().signOut();
                                  Toast.makeText(v.getContext(), R.string.modificacionCorreo, Toast.LENGTH_SHORT).show();
                                  v.getContext()
                                    .startActivity(
                                        new Intent(v.getContext(), BienvenidaActivity.class));
                              }
                            }
                          });
                }
              });
    }
  }

  public static void comprobarLogin(View v) {
      /*Funcion que comprueba si es la primera vez que se inicia con este usuario*/
    LoginActivity login = new LoginActivity();
    String id = userAuth.getCurrentUser().getUid();
    DocumentReference ref = instancia.collection("Users").document(id);
    ref.get()
        .addOnSuccessListener(
            new OnSuccessListener<DocumentSnapshot>() {
              @Override
              public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                  String firstLogin = documentSnapshot.getString("firstLogin");
                  login.accionLogin(v, firstLogin, "true");
                } else {
                  login.accionLogin(v, "err", "false");
                }
              }
            })
        .addOnFailureListener(
            new OnFailureListener() {
              @Override
              public void onFailure(@NonNull Exception e) {
                Log.d("e", String.valueOf(e));
                login.accionLogin(v, "err", "false");
              }
            });
  }

  public static void comprobarPass(Usuario usuario, View v, String PassAnterior) {
    /*valorNombre,valorPass... son los nuevos datos que el usuario quiere estableces, PassAnterior es la contraseña
     * que se tenia antes*/
    GestioncuentaActivity gest = new GestioncuentaActivity();
    String id = userAuth.getCurrentUser().getUid();
    DocumentReference ref = instancia.collection("Users").document(id);
    ref.get()
        .addOnSuccessListener(
            new OnSuccessListener<DocumentSnapshot>() {
              @Override
              public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                  String pass = documentSnapshot.getString("contraseña");
                  String passAnteriorHasheada = hashearMD5(PassAnterior);

                  if (pass.equals(passAnteriorHasheada)) {
                    gest.respuestaTestPass(usuario, PassAnterior, v, "true");
                  } else {
                    gest.respuestaTestPass(usuario, PassAnterior, v, "false");
                  }
                }
              }
            })
        .addOnFailureListener(
            new OnFailureListener() {
              @Override
              public void onFailure(@NonNull Exception e) {
                Log.d("e", String.valueOf(e));
                gest.respuestaTestPass(usuario, PassAnterior, v, "false");
              }
            });
  }

  public static void comprobarPass(View v, String valorIntroducido) {
      /*Funcion que comprueba la contraseña antes de ejecutar el borrado del usuario*/
    GestioncuentaActivity gest = new GestioncuentaActivity();
    String id = userAuth.getCurrentUser().getUid();
    DocumentReference ref = instancia.collection("Users").document(id);
    ref.get()
        .addOnSuccessListener(
            new OnSuccessListener<DocumentSnapshot>() {
              @Override
              public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                  String pass = documentSnapshot.getString("contraseña");
                  String valorIntroducidoHasheado = hashearMD5(valorIntroducido);
                  if (pass.equals(valorIntroducidoHasheado)) {
                    borrarSesionUsuario(v);
                  } else {
                    gest.respuestaBorrado(v, "false");
                  }
                }
              }
            })
        .addOnFailureListener(
            new OnFailureListener() {
              @Override
              public void onFailure(@NonNull Exception e) {
                Log.d("e", String.valueOf(e));
              }
            });
  }

  /*Borrado esta implementado en dos funciones, la primera se encarga de eliminar el documento
  * asociado a un usuario. La segunda se encarga de borrar la autenticacion de firebase*/
  public static void borrarSesionUsuario(View v) {
    FirebaseUser usuario = userAuth.getCurrentUser();
    String id = usuario.getUid();
    DocumentReference ref = instancia.collection("Users").document(id);
    borrarDatosUsuario(ref);
    usuario
        .delete()
        .addOnCompleteListener(
            new OnCompleteListener<Void>() {
              @Override
              public void onComplete(@NonNull Task<Void> task) {
                GestioncuentaActivity gest = new GestioncuentaActivity();
                if (task.isSuccessful()) {
                  gest.respuestaBorrado(v, "true");
                } else {
                  gest.respuestaBorrado(v, "false");
                }
              }
            });
  }

  public static void borrarDatosUsuario(DocumentReference ref) {
    ref.delete()
        .addOnCompleteListener(
            new OnCompleteListener<Void>() {
              @Override
              public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                  Log.i("correcto", "Datos eliminados");
                } else {
                  Log.e("error", "Fallos al eliminar");
                }
              }
            });
  }
}
