package com.tfg.mifeed.controlador.firebase;

import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.tfg.mifeed.controlador.activities.Activities.GestioncuentaActivity;
import com.tfg.mifeed.controlador.activities.Activities.LoginActivity;
import com.tfg.mifeed.controlador.activities.Activities.RegistroActivity;
import com.tfg.mifeed.controlador.activities.Activities.ResetContrasenha;
import com.tfg.mifeed.modelo.Usuario;

import java.util.HashMap;
import java.util.Map;

public class FirebaseServices {

  public static void ejecutarLogin(
      FirebaseAuth mAuth, boolean emailSent, String email, String pass, View v) {
    Log.d("enviado", String.valueOf(emailSent));
    mAuth
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
                      FirebaseServices.mandarEmailVerificacion(
                          FirebaseAuth.getInstance().getCurrentUser());
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

  public static void ejecutarRegistro(
      FirebaseFirestore firestore, FirebaseAuth mAuth, Usuario usuario, View v) {
    RegistroActivity registroActivity = new RegistroActivity();
    mAuth
        .createUserWithEmailAndPassword(usuario.getEmail(), usuario.getContraseña())
        .addOnCompleteListener(
            new OnCompleteListener<AuthResult>() {
              @Override
              public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                  Map<String, Object> user = new HashMap<>();
                  user.put("id", mAuth.getCurrentUser().getUid());
                  user.put("nombre", usuario.getNombre());
                  user.put("correo", usuario.getEmail());
                  user.put("contraseña", usuario.getContraseña());
                  user.put("firstLogin","true");
                  firestore
                      .collection("Users")
                      .document(mAuth.getCurrentUser().getUid())
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

  public static void mandarEmailVerificacion(FirebaseUser user) {
    user.sendEmailVerification();
  }

  public static void resetEmail(String email, FirebaseAuth auth, View v) {
    ResetContrasenha rst = new ResetContrasenha();
    auth.sendPasswordResetEmail(email)
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

  public static void getInfoUsuario(String userID, FirebaseFirestore db, View v) {
    GestioncuentaActivity ges = new GestioncuentaActivity();
    DocumentReference ref = db.collection("Users").document(userID);
    ref.get()
        .addOnSuccessListener(
            new OnSuccessListener<DocumentSnapshot>() {
              @Override
              public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                  String nombre = documentSnapshot.getString("nombre");
                  String correo = documentSnapshot.getString("correo");
                  String pass = documentSnapshot.getString("contraseña");
                  ges.respuestaDatosUsuario(nombre, correo, pass, v);

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

  public static void comprobarLogin(FirebaseFirestore db, String userID, View v){
      LoginActivity login = new LoginActivity();
      DocumentReference ref = db.collection("Users").document(userID);
      ref.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
          @Override
          public void onSuccess(DocumentSnapshot documentSnapshot) {
              if(documentSnapshot.exists()){
                  String firstLogin = documentSnapshot.getString("firstLogin");
                  login.accionLogin(v,firstLogin,"true");
              }else{
                  login.accionLogin(v,"err","false");
              }
          }
      }).addOnFailureListener(new OnFailureListener() {
          @Override
          public void onFailure(@NonNull Exception e) {
              Log.d("e", String.valueOf(e));
              login.accionLogin(v,"err", "false");
          }
      });
  }

  public static void borrarSesionUsuario(
      FirebaseUser usr, View v, FirebaseFirestore db, String userID) {

    DocumentReference ref = db.collection("Users").document(userID);
    borrarDatosUsuario(ref);
    usr.delete()
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
