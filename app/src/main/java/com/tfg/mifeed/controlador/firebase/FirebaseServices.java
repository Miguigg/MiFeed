package com.tfg.mifeed.controlador.firebase;

import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.tfg.mifeed.modelo.Usuario;
import com.tfg.mifeed.view.LoginActivity;
import com.tfg.mifeed.view.RegistroActivity;
import com.tfg.mifeed.view.ResetContrasenha;

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
                  firestore
                      .collection("Users")
                      .add(user)
                      .addOnCompleteListener(
                          new OnCompleteListener<DocumentReference>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentReference> task) {
                              if (task.isSuccessful()) {
                                registroActivity.respuestaRegistro("valido",v);
                              }else{
                                registroActivity.respuestaRegistro("NoValido",v);
                              }
                            }
                          });
                }else{
                  try {
                    throw task.getException();
                  } catch(FirebaseAuthUserCollisionException e) {
                    registroActivity.respuestaRegistro("yaExiste",v);
                  } catch(Exception e) {
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
}
