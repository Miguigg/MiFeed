package com.tfg.mifeed.controlador.firebase;

import static com.tfg.mifeed.controlador.utilidades.Validaciones.encriptarPass;

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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.tfg.mifeed.R;
import com.tfg.mifeed.controlador.activities.Activities.BienvenidaActivity;
import com.tfg.mifeed.controlador.activities.Activities.GestionCuenta.GestioncuentaActivity;import com.tfg.mifeed.controlador.activities.Activities.GestionCuenta.LoginActivity;
import com.tfg.mifeed.controlador.activities.Activities.GestionCuenta.RegistroActivity;
import com.tfg.mifeed.controlador.activities.Activities.GestionCuenta.ResetContrasenha;
import com.tfg.mifeed.controlador.activities.Activities.GestionCuenta.SeleccionMediosActivity;
import com.tfg.mifeed.controlador.activities.Activities.GestionCuenta.SeleccionTemasActivity;
import com.tfg.mifeed.controlador.activities.Activities.Prensa.FragmentsPrensa.CategoriasFragment;import com.tfg.mifeed.controlador.activities.Activities.Prensa.FragmentsPrensa.FavoritosFragment;import com.tfg.mifeed.controlador.activities.Activities.Prensa.FragmentsPrensa.ImportantesFragment;import com.tfg.mifeed.modelo.Usuario;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;import java.util.Objects;

public class FirebaseGestionUsuario {
  private static FirebaseFirestore instancia;

  private static FirebaseAuth userAuth;

  public FirebaseGestionUsuario() {
    inicioServicios();
  }

  public static void inicioServicios() {
    if (instancia == null) {
      instancia = FirebaseFirestore.getInstance();
    }

    if (userAuth == null) {
      userAuth = FirebaseAuth.getInstance();
    }
  }

  public static boolean checkLogin() {
    if (userAuth == null) {
      return false;
    }
    return true;
  }

  public static void ejecutarLogin(boolean emailSent, String email, String pass, View v) {
      /**
       * Con los datos introducidos por el usuario el sistema intenta acceder a la aplicacion
       * */
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
                      FirebaseGestionUsuario.mandarEmailVerificacion(v);
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
                login.accionLogin(v, "err", "false");
              }
            });
  }

  public static void ejecutarRegistro(Usuario usuario) {
      /*
      * Recibe un objeto con los datos del usuario validados para mandarlos a firebase
      * */
    RegistroActivity registroActivity = new RegistroActivity();
    userAuth
        .createUserWithEmailAndPassword(usuario.getEmail(), usuario.getContraseña())
        .addOnCompleteListener(
            new OnCompleteListener<AuthResult>() {
              @Override
              public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                  ArrayList<String> historial = new ArrayList<>();
                  Map<String, Object> user = new HashMap<>();
                  user.put("id", userAuth.getCurrentUser().getUid());
                  user.put("nombre", usuario.getNombre());
                  user.put("correo", usuario.getEmail());
                  user.put(
                      "contraseña",
                      encriptarPass(
                          usuario.getContraseña())); // insertamos la contraseña encriptada
                  user.put("firstLogin", "true");
                  user.put("notificaciones", "true");
                  user.put("guardarHistorial", "true");
                  user.put("historial", Arrays.asList(historial.toArray()));
                  instancia
                      .collection("Users")
                      .document(userAuth.getCurrentUser().getUid())
                      .set(user)
                      .addOnCompleteListener(
                          new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                              if (task.isSuccessful()) {
                                registroActivity.respuestaRegistro("valido");

                              } else {
                                registroActivity.respuestaRegistro("NoValido");
                              }
                            }
                          });
                } else {
                  try {
                    throw task.getException();
                  } catch (FirebaseAuthUserCollisionException e) {
                    registroActivity.respuestaRegistro("yaExiste");
                  } catch (Exception e) {
                    Log.e("TAG", e.getMessage());
                  }
                }
              }
            });
  }

  public static void mandarEmailVerificacion(View v) {
      /*
      * Cuando el usuario aun no ha validado el email, le manda el email de validacion
      * */
    userAuth
        .getCurrentUser()
        .sendEmailVerification()
        .addOnCompleteListener(
            new OnCompleteListener<Void>() {
              @Override
              public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                  Log.d("envioCorrecto", "Email enviado");
                } else {
                  Toast.makeText(v.getContext(), R.string.errEmailNoValido, Toast.LENGTH_LONG)
                      .show();
                }
              }
            });
  }

  public static void resetEmail(String email) {
      /*
      * Recibe el email introducido y, si coincide con alguno de la base de datos, le manda el email para resetear la contraseña
      * */
    ResetContrasenha rst = new ResetContrasenha();
    userAuth
        .sendPasswordResetEmail(email)
        .addOnCompleteListener(
            new OnCompleteListener<Void>() {
              @Override
              public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                  rst.validacionOK(task.isSuccessful());
                } else {
                  rst.validacionOK(task.isSuccessful());
                }
              }
            });
  }

  public static void setTemasUsuario(ArrayList<String> temas) {
      /*
      * Recibe un arraylist con los temas seleccionados por el usuario y los guarda en firebase
      * */
    SeleccionTemasActivity seleccionTemasActivity = new SeleccionTemasActivity();
    String id = userAuth.getCurrentUser().getUid();
    DocumentReference ref = instancia.collection("Users").document(id);
    Map<String, Object> listaTemas = new HashMap<>();
    listaTemas.put("temas", Arrays.asList(temas.toArray()));
    ref.update(listaTemas)
        .addOnCompleteListener(
            new OnCompleteListener<Void>() {
              @Override
              public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                  seleccionTemasActivity.respuestaSetTemas("true");
                } else {
                  seleccionTemasActivity.respuestaSetTemas("false");
                }
              }
            });
  }

  public static void setMediosUsuario(ArrayList<String> medios, ArrayList<String> dominios) {
      /*
      * Recibe el nombre y el dominio de los medios seleccionados por el usuario y los guarda en firebase
      * */
    for (int i = 0; i < medios.size(); i++) {
      Log.d("medio", medios.get(i));
    }
    String id = userAuth.getCurrentUser().getUid();
    SeleccionMediosActivity seleccionMediosActivity = new SeleccionMediosActivity();
    DocumentReference ref = instancia.collection("Users").document(id);
    Map<String, Object> listaMedios = new HashMap<>();
    Map<String, Object> listaDominios = new HashMap<>();
    listaMedios.put("medios", Arrays.asList(medios.toArray()));
    listaDominios.put("dominios", Arrays.asList(dominios.toArray()));
    ref.update(listaMedios)
        .addOnCompleteListener(
            new OnCompleteListener<Void>() {
              @Override
              public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                  ref.update(listaDominios)
                      .addOnCompleteListener(
                          new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                              if (task.isSuccessful()) {
                                seleccionMediosActivity.respuestaInsercion("true");
                              } else {
                                seleccionMediosActivity.respuestaInsercion("false");
                              }
                            }
                          });
                } else {
                  seleccionMediosActivity.respuestaInsercion("false");
                }
              }
            });
  }

  public static void setFirstLoginFalse() {
      /*
      * Cuando el usuario se loguea por primera vez el sistema actualiza su estado en firebase
      * */
    String id = userAuth.getCurrentUser().getUid();
    DocumentReference ref = instancia.collection("Users").document(id);
    Map<String, Object> valor = new HashMap<>();
    valor.put("firstLogin", "false");
    ref.update(valor).isSuccessful();
  }

  public static void editarUsuario(Usuario usuario, String passAnterior, View v) {
      /*
      * Recibe los datos nuevos introducidos por el usuario y los actualiza en firebase
      * */
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
                  Toast.makeText(v.getContext(), R.string.modificacionCuenta, Toast.LENGTH_SHORT)
                      .show();
                } else {
                  Toast.makeText(v.getContext(), R.string.errModificarDatos, Toast.LENGTH_SHORT)
                      .show();
                }
              }
            });

    String id = userAuth.getCurrentUser().getUid();

    DocumentReference ref = instancia.collection("Users").document(id);
    Map<String, Object> user = new HashMap<>();
    user.put("nombre", usuario.getNombre());
    user.put("contraseña", encriptarPass(usuario.getContraseña()));
    user.put("firstLogin", "false");
    if (usuario.isNotificaciones()) {
      user.put("notificaciones", "true");
    } else {
      user.put("notificaciones", "false");
    }
    if (usuario.isEtiquetasNube()) {
      user.put("guardarHistorial", "true");
    } else {
      user.put("guardarHistorial", "false");
    }

    ref.update(user)
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
      Log.d("info", "Modificando Email");
      userAuth
          .getCurrentUser()
          .reauthenticate(
              EmailAuthProvider.getCredential(userAuth.getCurrentUser().getEmail(), passAnterior))
          .addOnCompleteListener(
              new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                  if (task.isSuccessful()) {
                    Toast.makeText(
                            v.getContext(), R.string.errModificacionCorreo, Toast.LENGTH_SHORT)
                        .show();
                    Log.d("exito", "Modificación exitosa");
                  } else {
                    Toast.makeText(
                            v.getContext(), R.string.errModificacionEmail, Toast.LENGTH_SHORT)
                        .show();
                    Log.e("fracaso", "Error de sesion");
                  }

                  FirebaseAuth.getInstance()
                      .getCurrentUser()
                      .updateEmail(usuario.getEmail())
                      .addOnCompleteListener(
                          new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                              if (task.isSuccessful()) {
                                DocumentReference ref =
                                    FirebaseFirestore.getInstance()
                                        .collection("Users")
                                        .document(id);
                                Map<String, Object> user = new HashMap<>();
                                user.put("correo", usuario.getEmail());
                                ref.update(user)
                                    .addOnCompleteListener(
                                        new OnCompleteListener<Void>() {
                                          @Override
                                          public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                              Log.d("Correo", "Modificado con exito");
                                            } else {
                                              Log.d("Correo", "Error Correo");
                                            }
                                          }
                                        });
                                FirebaseAuth.getInstance().signOut();
                                Toast.makeText(
                                        v.getContext(),
                                        R.string.errModificacionCorreo,
                                        Toast.LENGTH_SHORT)
                                    .show();
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

    public static void getInfoUsuario(View v) {
        /*
         * Obtiene el nombre,correo y si tiene activas las notificaciones y el guardado del historial
         * */
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
                                    String nube = documentSnapshot.getString("guardarHistorial");
                                    ges.respuestaDatosUsuario(nombre, correo, notificaciones, nube, v);
                                } else {
                                    ges.respuestaDatosUsuario(v);
                                }
                            }
                        })
                .addOnFailureListener(
                        new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                ges.respuestaDatosUsuario(v);
                            }
                        });
    }

    public static void getMediosUsuario() {
        /*
         * Obtiene los medios que el usuario ya tiene en la base de datos
         * */
        FavoritosFragment favoritosFragment = new FavoritosFragment();
        String id = Objects.requireNonNull(userAuth.getCurrentUser()).getUid();
        DocumentReference ref = instancia.collection("Users").document(id);
        ref.get()
                .addOnSuccessListener(
                        new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                if (documentSnapshot.exists()) {
                                    ArrayList<String> listaMedios =
                                            (ArrayList<String>) documentSnapshot.get("medios");
                                    favoritosFragment.respuestaListaMedios(listaMedios, "true");
                                } else {
                                    ArrayList<String> listaMedios = new ArrayList<>();
                                    favoritosFragment.respuestaListaMedios(listaMedios, "false");
                                }
                            }
                        });
    }

    public static void getTemasUsuario(View v, String activity) {
        /*
         * Obtiene los temas que el usuario ya tiene en la base de datos
         * */
        String id = userAuth.getCurrentUser().getUid();
        DocumentReference ref = instancia.collection("Users").document(id);
        ref.get()
                .addOnSuccessListener(
                        new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                if (documentSnapshot.exists()) {
                                    ArrayList<String> listaTemas = (ArrayList<String>) documentSnapshot.get("temas");
                                    if (activity.equals("favoritos")) {
                                        FirebaseGestionUsuario.respuestaFavoritos(listaTemas, "true");
                                    } else {
                                        FirebaseGestionUsuario.respuestaCategorias(v, listaTemas, "true");
                                    }
                                } else {
                                    if (activity.equals("favoritos")) {
                                        ArrayList<String> listaTemas = new ArrayList<>();
                                        FirebaseGestionUsuario.respuestaFavoritos(listaTemas, "false");
                                    } else {
                                        ArrayList<String> listaTemas = new ArrayList<>();
                                        FirebaseGestionUsuario.respuestaCategorias(v, listaTemas, "false");
                                    }
                                }
                            }
                        });
    }

    public static void respuestaFavoritos(ArrayList<String> listaTemas, String codigo) {
        FavoritosFragment favoritosFragment = new FavoritosFragment();
        favoritosFragment.respuestaListaTemas(listaTemas, codigo);
    }

    public static void respuestaCategorias(View v, ArrayList<String> listaTemas, String codigo) {
        CategoriasFragment categoriasFragment = new CategoriasFragment();
        categoriasFragment.rellenarCategorias(v, listaTemas, codigo);
    }

    public static void getDominios(View view) {
        /*
         * Obtiene una lista de todos los dominios en la base de dayos
         * */
        ImportantesFragment importantesFragment = new ImportantesFragment();
        String id = userAuth.getCurrentUser().getUid();
        DocumentReference ref = instancia.collection("Users").document(id);
        ref.get()
                .addOnSuccessListener(
                        new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                if (documentSnapshot.exists()) {
                                    ArrayList<String> listaDominios =
                                            (ArrayList<String>) documentSnapshot.get("dominios");
                                    importantesFragment.respuestaListaDominios(listaDominios, "true", view);
                                } else {
                                    ArrayList<String> listaDominios = new ArrayList<>();
                                    importantesFragment.respuestaListaDominios(listaDominios, "false", view);
                                }
                            }
                        });
    }
}
