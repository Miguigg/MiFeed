package com.tfg.mifeed.controlador.firebase;

import static android.text.Html.fromHtml;
import static com.tfg.mifeed.controlador.utilidades.Validaciones.hashearMD5;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
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
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.tfg.mifeed.R;
import com.tfg.mifeed.controlador.activities.Activities.BienvenidaActivity;
import com.tfg.mifeed.controlador.activities.Activities.GestionCuenta.GestioncuentaActivity;
import com.tfg.mifeed.controlador.activities.Activities.GestionCuenta.LoginActivity;
import com.tfg.mifeed.controlador.activities.Activities.GestionCuenta.RegistroActivity;
import com.tfg.mifeed.controlador.activities.Activities.GestionCuenta.ResetContrasenha;
import com.tfg.mifeed.controlador.activities.Activities.GestionCuenta.SeleccionMediosActivity;
import com.tfg.mifeed.controlador.activities.Activities.GestionCuenta.SeleccionTemasActivity;
import com.tfg.mifeed.controlador.activities.Activities.Podcast.FragmentsPodcast.BibliotecaFragment;
import com.tfg.mifeed.controlador.activities.Activities.Podcast.FragmentsPodcast.MasTardeFragment;
import com.tfg.mifeed.controlador.activities.Activities.Prensa.FragmentsPrensa.CategoriasFragment;
import com.tfg.mifeed.controlador.activities.Activities.Prensa.FragmentsPrensa.EtiquetasFragment;
import com.tfg.mifeed.controlador.activities.Activities.Prensa.FragmentsPrensa.FavoritosFragment;
import com.tfg.mifeed.controlador.activities.Activities.Prensa.FragmentsPrensa.ImportantesFragment;
import com.tfg.mifeed.controlador.activities.Activities.Prensa.HistorialActivity;
import com.tfg.mifeed.controlador.activities.Activities.Prensa.NoticiaActivity;
import com.tfg.mifeed.modelo.Episodio;
import com.tfg.mifeed.modelo.Etiqueta;
import com.tfg.mifeed.modelo.Podcast;
import com.tfg.mifeed.modelo.Usuario;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class FirebaseServices {

    @SuppressLint("StaticFieldLeak")
    private static FirebaseFirestore instancia;

    private static FirebaseAuth userAuth;

    public FirebaseServices() {
        inicioServicios();
    }

    public static void inicioServicios() {
        if (instancia == null) {
            instancia = FirebaseFirestore.getInstance();
            userAuth = FirebaseAuth.getInstance();
        }
    }

    public static void ejecutarLogin(boolean emailSent, String email, String pass, View v) {
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
                                            FirebaseServices.mandarEmailVerificacion(v);
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
                                Log.d("e", String.valueOf(e));
                                login.accionLogin(v, "err", "false");
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
                                    ArrayList<String> historial = new ArrayList<>();
                                    ArrayList<String> masTarde = new ArrayList<>();
                                    Map<String, Object> user = new HashMap<>();
                                    user.put("id", userAuth.getCurrentUser().getUid());
                                    user.put("nombre", usuario.getNombre());
                                    user.put("correo", usuario.getEmail());
                                    user.put(
                                            "contraseña",
                                            hashearMD5(usuario.getContraseña())); // insertamos la contraseña encriptada
                                    user.put("firstLogin", "true");
                                    user.put("notificaciones", "true");
                                    user.put("guardarHistorial", "true");
                                    user.put("historial", Arrays.asList(historial.toArray()));
                                    user.put("bibliotecaPodcast", Arrays.asList(masTarde.toArray()));
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

    public static void mandarEmailVerificacion(View v) {
        userAuth.getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Log.d("envioCorrecto", "Email enviado");
                } else {
                    Toast.makeText(v.getContext(), R.string.errEmailNoValido, Toast.LENGTH_LONG).show();
                }
            }
        });
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
                                } else {
                                    rst.validacionOK(task.isSuccessful(), v);
                                }
                            }
                        });
    }

    public static void setTemasUsuario(ArrayList<String> temas, View v) {
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
                                    seleccionTemasActivity.respuestaSetTemas("true", v);
                                } else {
                                    Toast.makeText(v.getContext(), R.string.errSesion, Toast.LENGTH_SHORT).show();
                                    seleccionTemasActivity.respuestaSetTemas("false", v);
                                }
                            }
                        });
    }

    public static void setMediosUsuario(
            ArrayList<String> medios, ArrayList<String> dominios, View v) {
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
                                                                seleccionMediosActivity.respuestaInsercion("true", v);
                                                            } else {
                                                                seleccionMediosActivity.respuestaInsercion("false", v);
                                                                Toast.makeText(
                                                                                v.getContext(), R.string.errSesion, Toast.LENGTH_SHORT)
                                                                        .show();
                                                            }
                                                        }
                                                    });
                                } else {
                                    seleccionMediosActivity.respuestaInsercion("false", v);
                                    Toast.makeText(v.getContext(), R.string.errSesion, Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
    }

    public static void setFirstLoginFalse() {
        String id = userAuth.getCurrentUser().getUid();
        DocumentReference ref = instancia.collection("Users").document(id);
        Map<String, Object> valor = new HashMap<>();
        valor.put("firstLogin", "false");
        ref.update(valor).isSuccessful();
    }

    public static void getMedios(View v) {
        SeleccionMediosActivity seleccionMediosActivity = new SeleccionMediosActivity();
        ArrayList<String> medios = new ArrayList<>();
        ArrayList<String> dominios = new ArrayList<>();
        FirebaseFirestore.getInstance()
                .collection("Medios")
                .get()
                .addOnCompleteListener(
                        new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                                        medios.add(documentSnapshot.getString("Nombre"));
                                        dominios.add(documentSnapshot.getString("url"));
                                    }
                                    seleccionMediosActivity.setMedios(medios, dominios, v);
                                } else {
                                    Toast.makeText(v.getContext(), R.string.errConn, Toast.LENGTH_LONG).show();
                                    Log.e("errMedios", String.valueOf(task.getException()));
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
                                    Toast.makeText(v.getContext(), R.string.modificacionCuenta, Toast.LENGTH_SHORT)
                                            .show();
                                    Log.d("Contraseña", "contraseña modificada");
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
        user.put("contraseña", hashearMD5(usuario.getContraseña()));
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
                                        Toast.makeText(v.getContext(), R.string.modificacionCorreo, Toast.LENGTH_SHORT)
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
                                                                                R.string.modificacionCorreo,
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

    public static void getMediosUsuario(View v) {
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
                                    favoritosFragment.respuestaListaMedios(listaMedios, "true", v);
                                } else {
                                    ArrayList<String> listaMedios = new ArrayList<>();
                                    favoritosFragment.respuestaListaMedios(listaMedios, "false", v);
                                }
                            }
                        });
    }

    public static void getTemasUsuario(View v, String activity) {
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
                                        FirebaseServices.respuestaFavoritos(v, listaTemas, "true");
                                    } else {
                                        FirebaseServices.respuestaCategorias(v, listaTemas, "true");
                                    }
                                } else {
                                    if (activity.equals("favoritos")) {
                                        ArrayList<String> listaTemas = new ArrayList<>();
                                        FirebaseServices.respuestaFavoritos(v, listaTemas, "false");
                                    } else {
                                        ArrayList<String> listaTemas = new ArrayList<>();
                                        FirebaseServices.respuestaCategorias(v, listaTemas, "false");
                                    }
                                }
                            }
                        });
    }

    public static void respuestaFavoritos(View v, ArrayList<String> listaTemas, String codigo) {
        FavoritosFragment favoritosFragment = new FavoritosFragment();
        favoritosFragment.respuestaListaTemas(listaTemas, codigo, v);
    }

    public static void respuestaCategorias(View v, ArrayList<String> listaTemas, String codigo) {
        CategoriasFragment categoriasFragment = new CategoriasFragment();
        categoriasFragment.rellenarCategorias(v, listaTemas, codigo);
    }

    public static void getDominios(View view) {
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

    public static void crearEtiqueta(String nombre, View v) {
        EtiquetasFragment etiquetasFragment = new EtiquetasFragment();
        Map<String, Object> etiqueta = new HashMap<>();
        ArrayList<String> urls = new ArrayList<>();
        ArrayList<String> titulos = new ArrayList<>();
        etiqueta.put("creador", userAuth.getCurrentUser().getUid());
        etiqueta.put("tituloEtiqueta", nombre);
        etiqueta.put("titulos", Arrays.asList(titulos.toArray()));
        etiqueta.put("urls", Arrays.asList(urls.toArray()));

        instancia
                .collection("Etiquetas")
                .document()
                .set(etiqueta)
                .addOnCompleteListener(
                        new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    etiquetasFragment.respuestaCreacionEtiqueta("true", v);
                                } else {
                                    etiquetasFragment.respuestaCreacionEtiqueta("false", v);
                                }
                            }
                        });
    }

    public static void deleteEtiqueta(String nombre, View v) {
        CollectionReference ref = instancia.collection("Etiquetas");
        ref.get()
                .addOnSuccessListener(
                        new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                boolean borrado = false;
                                for (int i = 0; i < queryDocumentSnapshots.getDocuments().size(); i++) {
                                    if (queryDocumentSnapshots
                                            .getDocuments()
                                            .get(i)
                                            .get("tituloEtiqueta")
                                            .equals(nombre)) {
                                        borrado = true;
                                        String id = queryDocumentSnapshots.getDocuments().get(i).getId();
                                        instancia
                                                .collection("Etiquetas")
                                                .document(id)
                                                .delete()
                                                .addOnCompleteListener(
                                                        new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if (task.isSuccessful()) {
                                                                    Toast.makeText(
                                                                                    v.getContext(),
                                                                                    R.string.eliminacionEtiqueta,
                                                                                    Toast.LENGTH_SHORT)
                                                                            .show();
                                                                    getEtiquetas(v);
                                                                } else {
                                                                    Toast.makeText(
                                                                                    v.getContext(),
                                                                                    R.string.errEliminacionEtiqueta,
                                                                                    Toast.LENGTH_SHORT)
                                                                            .show();
                                                                    getEtiquetas(v);
                                                                }
                                                            }
                                                        });
                                    }
                                }
                                if (!borrado) {
                                    Toast.makeText(
                                                    v.getContext(), R.string.errEtiquetaNoEncontrada, Toast.LENGTH_SHORT)
                                            .show();
                                }
                            }
                        });
    }

    public static void getNombresEtiquetas(View v, LayoutInflater inf) {
        NoticiaActivity noticiaActivity = new NoticiaActivity();
        CollectionReference ref = instancia.collection("Etiquetas");
        ArrayList<String> nombreEtiquetas = new ArrayList<>();
        String id = userAuth.getCurrentUser().getUid();

        ref.get()
                .addOnSuccessListener(
                        new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                for (int i = 0; i < queryDocumentSnapshots.getDocuments().size(); i++) {
                                    if (queryDocumentSnapshots.getDocuments().get(i).get("creador").equals(id)) {
                                        nombreEtiquetas.add(
                                                (String)
                                                        queryDocumentSnapshots.getDocuments().get(i).get("tituloEtiqueta"));
                                    }
                                }
                                if (nombreEtiquetas.size() > 0) {
                                    noticiaActivity.respuestaNombresEtiquetas("true", nombreEtiquetas, v, inf);
                                } else {
                                    noticiaActivity.respuestaNombresEtiquetas("false", nombreEtiquetas, v, inf);
                                }
                            }
                        });
    }

    public static void getEtiquetas(View v) {
        EtiquetasFragment etiquetasFragment = new EtiquetasFragment();
        CollectionReference ref = instancia.collection("Etiquetas");
        ArrayList<Etiqueta> etiquetasUsuario = new ArrayList<>();
        String id = userAuth.getCurrentUser().getUid();
        ref.get()
                .addOnSuccessListener(
                        new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                for (int i = 0; i < queryDocumentSnapshots.getDocuments().size(); i++) {
                                    if (queryDocumentSnapshots.getDocuments().get(i).get("creador").equals(id)) {
                                        String tituloEtiqueta =
                                                queryDocumentSnapshots.getDocuments().get(i).getString("tituloEtiqueta");
                                        @SuppressWarnings("unchecked")
                                        ArrayList<String> titulosNoticias =
                                                (ArrayList<String>)
                                                        queryDocumentSnapshots.getDocuments().get(i).get("titulos");
                                        @SuppressWarnings("unchecked")
                                        ArrayList<String> urlsNoticias =
                                                (ArrayList<String>)
                                                        queryDocumentSnapshots.getDocuments().get(i).get("urls");
                                        Etiqueta etiqueta = new Etiqueta(tituloEtiqueta, urlsNoticias, titulosNoticias);
                                        etiquetasUsuario.add(etiqueta);
                                    }
                                }
                                if (etiquetasUsuario.size() > 0) {
                                    etiquetasFragment.respuestaGetEtiquetas("true", etiquetasUsuario, v);
                                } else {
                                    etiquetasFragment.respuestaGetEtiquetas("false", etiquetasUsuario, v);
                                }
                            }
                        });
    }

    public static void eliminarUrlLista(String url, String nombre, String nombreEtiqueta) {
        CollectionReference ref = instancia.collection("Etiquetas");
        ref.get()
                .addOnSuccessListener(
                        new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                for (int i = 0; i < queryDocumentSnapshots.getDocuments().size(); i++) {
                                    if (queryDocumentSnapshots
                                            .getDocuments()
                                            .get(i)
                                            .get("tituloEtiqueta")
                                            .equals(nombreEtiqueta)) {

                                        String id = queryDocumentSnapshots.getDocuments().get(i).getId();
                                        instancia
                                                .collection("Etiquetas")
                                                .document(id)
                                                .get()
                                                .addOnSuccessListener(
                                                        new OnSuccessListener<DocumentSnapshot>() {

                                                            @Override
                                                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                                ArrayList<String> listaUrls =
                                                                        (ArrayList<String>) documentSnapshot.get("urls");
                                                                ArrayList<String> listaTitulos =
                                                                        (ArrayList<String>) documentSnapshot.get("titulos");
                                                                for (int i = 0; i < listaUrls.size(); i++) {
                                                                    if (listaUrls.get(i).equals(url)) {
                                                                        listaUrls.remove(i);
                                                                        listaTitulos.remove(i);
                                                                    }
                                                                }
                                                                actualizarUrlsyTitulos(listaUrls, listaTitulos, nombreEtiqueta);
                                                            }
                                                        });
                                    }
                                }
                            }
                        });
    }

    public static void actualizarUrlsyTitulos(
            ArrayList<String> listaUrls, ArrayList<String> listaTitulos, String nombreEtiqueta) {
        CollectionReference ref = instancia.collection("Etiquetas");
        String id = userAuth.getCurrentUser().getUid();
        ref.get()
                .addOnSuccessListener(
                        new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                for (int i = 0; i < queryDocumentSnapshots.getDocuments().size(); i++) {
                                    if (queryDocumentSnapshots.getDocuments().get(i).get("creador").equals(id)
                                            && queryDocumentSnapshots
                                            .getDocuments()
                                            .get(i)
                                            .get("tituloEtiqueta")
                                            .equals(nombreEtiqueta)) {
                                        String idDocumento = queryDocumentSnapshots.getDocuments().get(i).getId();
                                        Map<String, Object> etiqueta = new HashMap<>();
                                        etiqueta.put("titulos", Arrays.asList(listaTitulos.toArray()));
                                        etiqueta.put("urls", Arrays.asList(listaUrls.toArray()));
                                        instancia.collection("Etiquetas").document(idDocumento).update(etiqueta);
                                    }
                                }
                            }
                        });
    }

    public static void insertarUrlEtiqueta(String url, String nombreSitio, String nombreEtiqueta) {
        CollectionReference ref = instancia.collection("Etiquetas");
        String id = userAuth.getCurrentUser().getUid();
        ref.get()
                .addOnSuccessListener(
                        new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                for (int i = 0; i < queryDocumentSnapshots.getDocuments().size(); i++) {
                                    if (queryDocumentSnapshots.getDocuments().get(i).get("creador").equals(id)
                                            && queryDocumentSnapshots
                                            .getDocuments()
                                            .get(i)
                                            .get("tituloEtiqueta")
                                            .equals(nombreEtiqueta)) {
                                        String idDocumento = queryDocumentSnapshots.getDocuments().get(i).getId();
                                        @SuppressWarnings("unchecked")
                                        ArrayList<String> titulosNoticias =
                                                (ArrayList<String>)
                                                        queryDocumentSnapshots.getDocuments().get(i).get("titulos");
                                        @SuppressWarnings("unchecked")
                                        ArrayList<String> urlsNoticias =
                                                (ArrayList<String>)
                                                        queryDocumentSnapshots.getDocuments().get(i).get("urls");
                                        titulosNoticias.add(nombreSitio);
                                        urlsNoticias.add(url);
                                        Map<String, Object> etiqueta = new HashMap<>();
                                        etiqueta.put("creador", id);
                                        etiqueta.put("tituloEtiqueta", nombreEtiqueta);
                                        etiqueta.put("titulos", Arrays.asList(titulosNoticias.toArray()));
                                        etiqueta.put("urls", Arrays.asList(urlsNoticias.toArray()));
                                        instancia.collection("Etiquetas").document(idDocumento).update(etiqueta);
                                    }
                                }
                            }
                        });
    }

    public static void getHistorial(View v) {
        HistorialActivity historialActivity = new HistorialActivity();
        String id = userAuth.getCurrentUser().getUid();
        DocumentReference ref = instancia.collection("Users").document(id);
        ref.get()
                .addOnSuccessListener(
                        new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                @SuppressWarnings("unchecked")
                                ArrayList<String> historial = (ArrayList<String>) documentSnapshot.get("historial");
                                historialActivity.respuestaHistorial(historial, v);
                            }
                        });
    }

    public static void addParaMasTarde(Episodio episodio, String titulo, Context c, String idPodcast, String tituloPodcast) {
        String idUsuario = userAuth.getCurrentUser().getUid();
        Map<String, Object> episodioMasTarde = new HashMap<>();
        episodioMasTarde.put("usuario", idUsuario);
        episodioMasTarde.put("nombreCapitulo", titulo);
        episodioMasTarde.put("urlImagen", episodio.getImage());
        episodioMasTarde.put("urlAudio", episodio.getAudio());
        episodioMasTarde.put("idPodcast", idPodcast);
        episodioMasTarde.put("nombrePodcast", tituloPodcast);
        episodioMasTarde.put(
                "descripcion", tituloPodcast);
        CollectionReference ref = instancia.collection("Episodios");
        ArrayList<String> toret = new ArrayList<>();

        ref.get()
                .addOnSuccessListener(
                        new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                for (int i = 0; i < queryDocumentSnapshots.getDocuments().size(); i++) {
                                    if (queryDocumentSnapshots
                                            .getDocuments()
                                            .get(i)
                                            .get("usuario")
                                            .equals(idUsuario)) {
                                        String nombreCapitulo =
                                                (String) queryDocumentSnapshots.getDocuments().get(i).get("nombreCapitulo");
                                        String urlAudio =
                                                (String) queryDocumentSnapshots.getDocuments().get(i).get("urlAudio");
                                        String urlImagen =
                                                (String) queryDocumentSnapshots.getDocuments().get(i).get("urlImagen");
                                        String idPodcast =
                                                (String) queryDocumentSnapshots.getDocuments().get(i).get("idPodcast");
                                        String descripcion =
                                                (String) queryDocumentSnapshots.getDocuments().get(i).get("descripcion");
                                        String tituloPodcast = (String) queryDocumentSnapshots.getDocuments().get(i).get("nombrePodcast");
                                        Episodio episodio =
                                                new Episodio(nombreCapitulo, urlImagen, urlAudio, idPodcast, descripcion, tituloPodcast);
                                        toret.add(episodio.getTitle());
                                    }
                                }
                                if (!toret.contains(titulo)) {
                                    instancia
                                            .collection("Episodios")
                                            .document()
                                            .set(episodioMasTarde)
                                            .addOnCompleteListener(
                                                    new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if (task.isSuccessful()) {
                                                                Toast.makeText(c, R.string.txtAddPodcast, Toast.LENGTH_SHORT)
                                                                        .show();
                                                            } else {
                                                                Toast.makeText(c, R.string.errAddPodcast, Toast.LENGTH_SHORT)
                                                                        .show();
                                                            }
                                                        }
                                                    });
                                } else {
                                    Toast.makeText(c, R.string.errPodcastYaExiste, Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
    }

    public static void getPodcastMasTarde(View v) {
        MasTardeFragment masTardeFragment = new MasTardeFragment();
        String id = userAuth.getCurrentUser().getUid();
        CollectionReference ref = instancia.collection("Episodios");
        ArrayList<Episodio> toret = new ArrayList<>();
        ref.get()
                .addOnSuccessListener(
                        new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                for (int i = 0; i < queryDocumentSnapshots.getDocuments().size(); i++) {
                                    if (queryDocumentSnapshots.getDocuments().get(i).get("usuario").equals(id)) {
                                        String nombreCapitulo =
                                                (String) queryDocumentSnapshots.getDocuments().get(i).get("nombreCapitulo");
                                        String urlAudio =
                                                (String) queryDocumentSnapshots.getDocuments().get(i).get("urlAudio");
                                        String urlImagen =
                                                (String) queryDocumentSnapshots.getDocuments().get(i).get("urlImagen");
                                        String idPodcast =
                                                (String) queryDocumentSnapshots.getDocuments().get(i).get("idPodcast");
                                        String descripcion =
                                                (String) queryDocumentSnapshots.getDocuments().get(i).get("descripcion");
                                        String tituloPodcast = (String) queryDocumentSnapshots.getDocuments().get(i).get("nombrePodcast");
                                        Episodio episodio =
                                                new Episodio(nombreCapitulo, urlImagen, urlAudio, idPodcast, descripcion, tituloPodcast);
                                        toret.add(episodio);
                                    }
                                }
                                if (toret.size() > 0) {
                                    masTardeFragment.respuestaListaPodcast(toret, "true", v);
                                } else {
                                    masTardeFragment.respuestaListaPodcast(toret, "false", v);
                                }
                            }
                        });
    }

    public static void eliminarPodcastMastarde(String urlPodcast, Context c) {
        String id = userAuth.getCurrentUser().getUid();
        CollectionReference ref = instancia.collection("Episodios");

        ref.get()
                .addOnSuccessListener(
                        new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                for (int i = 0; i < queryDocumentSnapshots.getDocuments().size(); i++) {
                                    if (queryDocumentSnapshots.getDocuments().get(i).get("usuario").equals(id)
                                            && queryDocumentSnapshots
                                            .getDocuments()
                                            .get(i)
                                            .get("urlAudio")
                                            .equals(urlPodcast)) {
                                        String id = queryDocumentSnapshots.getDocuments().get(i).getId();
                                        instancia
                                                .collection("Episodios")
                                                .document(id)
                                                .delete()
                                                .addOnSuccessListener(
                                                        new OnSuccessListener<Void>() {
                                                            @Override
                                                            public void onSuccess(Void unused) {
                                                                Toast.makeText(c, R.string.episodioEliminado, Toast.LENGTH_SHORT)
                                                                        .show();
                                                            }
                                                        });
                                    }
                                }
                            }
                        });
    }

    public static void addPodcastBiblioteca(Podcast podcast, Context c) {
        String id = userAuth.getCurrentUser().getUid();
        CollectionReference ref = instancia.collection("Podcast");
        Map<String, Object> podcastSubidos = new HashMap<>();
        podcastSubidos.put("idPodcast", podcast.getId());
        podcastSubidos.put("imagen", podcast.getImage());
        podcastSubidos.put("titulo", podcast.getTitle_original());
        podcastSubidos.put("creador", id);
        podcastSubidos.put("descripcion", podcast.getDescripcion());
        ArrayList<String> ids = new ArrayList<>();
        ref.get()
                .addOnSuccessListener(
                        new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                for (int i = 0; i < queryDocumentSnapshots.size(); i++) {
                                    ids.add((String) queryDocumentSnapshots.getDocuments().get(i).get("idPodcast"));
                                }
                                if (!ids.contains(podcast.getId())) {
                                    instancia
                                            .collection("Podcast").document()
                                            .set(podcastSubidos)
                                            .addOnCompleteListener(
                                                    new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            Toast.makeText(c, R.string.txtAddPodcast, Toast.LENGTH_SHORT).show();
                                                        }
                                                    });
                                } else {
                                    Toast.makeText(c, R.string.errPodcastYaExiste, Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
    }

    public static void getPodcastBiblioteca() {
        BibliotecaFragment bibliotecaFragment = new BibliotecaFragment();
        String id = userAuth.getCurrentUser().getUid();
        CollectionReference ref = instancia.collection("Podcast");
        ArrayList<Podcast> toret = new ArrayList<>();
        ref.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (int i = 0; i < queryDocumentSnapshots.size(); i++) {
                    if (queryDocumentSnapshots.getDocuments().get(i).get("creador").equals(id)) {
                        String idPodcast = (String) queryDocumentSnapshots.getDocuments().get(i).get("idPodcast");
                        String imagen = (String) queryDocumentSnapshots.getDocuments().get(i).get("imagen");
                        String titulo = (String) queryDocumentSnapshots.getDocuments().get(i).get("titulo");
                        Podcast podcast = new Podcast(idPodcast, imagen, titulo, titulo);
                        toret.add(podcast);
                    }
                }
                if (toret.size() > 0) {
                    bibliotecaFragment.respuestaBiblioteca("true", toret);
                } else {
                    bibliotecaFragment.respuestaBiblioteca("false", toret);
                }
            }
        });
    }

    public static void eliminarPodcastBiblioteca(String idPodcast, Context c) {
        CollectionReference ref = instancia.collection("Podcast");
        ref.get()
                .addOnSuccessListener(
                        new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                for (int i = 0; i < queryDocumentSnapshots.size(); i++) {
                                    if (queryDocumentSnapshots
                                            .getDocuments()
                                            .get(i)
                                            .get("idPodcast")
                                            .equals(idPodcast)) {
                                        instancia
                                                .collection("Podcast")
                                                .document(queryDocumentSnapshots.getDocuments().get(i).getId())
                                                .delete()
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            Toast.makeText(c, R.string.txtDeletePodcast, Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                });
                                    }
                                }
                            }
                        });
    }

    public static void insertarHistorial(String url, View v) {
        String id = userAuth.getCurrentUser().getUid();
        DocumentReference ref = instancia.collection("Users").document(id);

        ref.get()
                .addOnSuccessListener(
                        new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                String guardarHistorial = documentSnapshot.getString("guardarHistorial");
                                switch (guardarHistorial) {
                                    case "true":
                                        ref.get()
                                                .addOnSuccessListener(
                                                        new OnSuccessListener<DocumentSnapshot>() {
                                                            @Override
                                                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                                @SuppressWarnings("unchecked")
                                                                ArrayList<String> historial =
                                                                        (ArrayList<String>) documentSnapshot.get("historial");
                                                                if (!historial.contains(url)) {
                                                                    historial.add(url);
                                                                }
                                                                Map<String, Object> historialNuevo = new HashMap<>();
                                                                historialNuevo.put("historial", Arrays.asList(historial.toArray()));
                                                                ref.update(historialNuevo)
                                                                        .addOnCompleteListener(
                                                                                new OnCompleteListener<Void>() {
                                                                                    @Override
                                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                                        if (task.isSuccessful()) {
                                                                                            Log.d("historial", "add realizado correctamente");
                                                                                        } else {
                                                                                            Toast.makeText(
                                                                                                            v.getContext(),
                                                                                                            R.string.errHistorial,
                                                                                                            Toast.LENGTH_SHORT)
                                                                                                    .show();
                                                                                        }
                                                                                    }
                                                                                });
                                                            }
                                                        });
                                    default:
                                        break;
                                }
                            }
                        });
    }

    public static void eliminarElementoHistorial(String url) {
        String id = userAuth.getCurrentUser().getUid();
        DocumentReference ref = instancia.collection("Users").document(id);
        ref.get()
                .addOnSuccessListener(
                        new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                ArrayList<String> historial = (ArrayList<String>) documentSnapshot.get("historial");
                                for (int i = 0; i < historial.size(); i++) {
                                    if (historial.get(i).equals(url)) {
                                        historial.remove(url);
                                    }
                                }
                                Map<String, Object> historialNuevo = new HashMap<>();
                                historialNuevo.put("historial", Arrays.asList(historial.toArray()));
                                ref.update(historialNuevo)
                                        .addOnCompleteListener(
                                                new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            Log.d("exito", "eliminacion del articulo correcta");
                                                        } else {
                                                            Log.e("err", "Error al eliminar el articulo");
                                                        }
                                                    }
                                                });
                            }
                        });
    }

    public static void eliminarTodoHistorial(View v) {
        String id = userAuth.getCurrentUser().getUid();
        ArrayList<String> historial = new ArrayList<>();
        Map<String, Object> historialNuevo = new HashMap<>();
        historialNuevo.put("historial", Arrays.asList(historial.toArray()));
        DocumentReference ref = instancia.collection("Users").document(id);
        ref.update(historialNuevo)
                .addOnCompleteListener(
                        new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(v.getContext(), R.string.txtHistorial, Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(v.getContext(), R.string.errHistorial, Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
    }
}
