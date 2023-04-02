package com.tfg.mifeed.controlador.firebase;

import static android.text.Html.fromHtml;
import static com.tfg.mifeed.controlador.utilidades.Validaciones.encriptarPass;

import android.annotation.SuppressLint;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.tfg.mifeed.controlador.activities.Activities.GestionCuenta.GestioncuentaActivity;
import com.tfg.mifeed.controlador.activities.Activities.GestionCuenta.SeleccionMediosActivity;
import com.tfg.mifeed.controlador.activities.Activities.Prensa.FragmentsPrensa.CategoriasFragment;
import com.tfg.mifeed.controlador.activities.Activities.Prensa.FragmentsPrensa.FavoritosFragment;
import com.tfg.mifeed.controlador.activities.Activities.Prensa.FragmentsPrensa.ImportantesFragment;
import com.tfg.mifeed.modelo.MediosModel;
import com.tfg.mifeed.modelo.Usuario;

import java.util.ArrayList;
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

  public static void getMedios() {
      /*
      * Obtiene la lista actual de medios disponibles en firebase
      * */
    SeleccionMediosActivity seleccionMediosActivity = new SeleccionMediosActivity();
    ArrayList<String> medios = new ArrayList<>();
    ArrayList<String> dominios = new ArrayList<>();
    ArrayList<MediosModel> datosMedios = new ArrayList<>();
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
                    MediosModel mediosModel = new MediosModel(documentSnapshot.getString("Nombre"),documentSnapshot.getString("url"), false);
                    datosMedios.add(mediosModel);
                  }
                  seleccionMediosActivity.setMedios(datosMedios, "true");
                } else {
                  seleccionMediosActivity.setMedios(datosMedios, "false");
                }
              }
            });
  }

  public static void comprobarPass(Usuario usuario, String passAnterior) {
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
                  String passAnteriorHasheada = encriptarPass(passAnterior);

                  if (pass.equals(passAnteriorHasheada)) {
                    gest.respuestaTestPass(usuario, passAnterior, "true");
                  } else {
                    gest.respuestaTestPass(usuario, passAnterior, "false");
                  }
                }
              }
            })
        .addOnFailureListener(
            new OnFailureListener() {
              @Override
              public void onFailure(@NonNull Exception e) {
                gest.respuestaTestPass(usuario, passAnterior, "false");
              }
            });
  }

  public static void comprobarPass(String valorIntroducido) {
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
                  String valorIntroducidoHasheado = encriptarPass(valorIntroducido);
                  if (pass.equals(valorIntroducidoHasheado)) {
                    borrarSesionUsuario();
                  } else {
                    gest.respuestaBorrado("false");
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
  public static void borrarSesionUsuario() {
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
                  gest.respuestaBorrado("true");
                } else {
                  gest.respuestaBorrado("false");
                }
              }
            });
  }

  public static void borrarDatosUsuario(DocumentReference ref) {
    FirebaseUser usuario = userAuth.getCurrentUser();
    String id = usuario.getUid();
    instancia
        .collection("Episodios")
        .get()
        .addOnSuccessListener(
            new OnSuccessListener<QuerySnapshot>() {
              @Override
              public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (int i = 0; i < queryDocumentSnapshots.size(); i++) {
                  if (queryDocumentSnapshots.getDocuments().get(i).get("usuario").equals(id)) {
                    String idDocumento = queryDocumentSnapshots.getDocuments().get(i).getId();
                    instancia.collection("Episodios").document(idDocumento).delete();
                  }
                }
              }
            });

    instancia
        .collection("Podcast")
        .get()
        .addOnSuccessListener(
            new OnSuccessListener<QuerySnapshot>() {
              @Override
              public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (int i = 0; i < queryDocumentSnapshots.size(); i++) {
                  if (queryDocumentSnapshots.getDocuments().get(i).get("creador").equals(id)) {
                    String idDocumento = queryDocumentSnapshots.getDocuments().get(i).getId();
                    instancia.collection("Podcast").document(idDocumento).delete();
                  }
                }
              }
            });

    instancia
        .collection("Etiquetas")
        .get()
        .addOnSuccessListener(
            new OnSuccessListener<QuerySnapshot>() {
              @Override
              public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (int i = 0; i < queryDocumentSnapshots.size(); i++) {
                  if (queryDocumentSnapshots.getDocuments().get(i).get("creador").equals(id)) {
                    String idDocumento = queryDocumentSnapshots.getDocuments().get(i).getId();
                    instancia.collection("Etiquetas").document(idDocumento).delete();
                  }
                }
              }
            });

    instancia.collection("Recordatorios").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
        @Override
        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
            for (int i = 0; i < queryDocumentSnapshots.size(); i++) {
                if (queryDocumentSnapshots.getDocuments().get(i).get("creador").equals(id)) {
                    String idDocumento = queryDocumentSnapshots.getDocuments().get(i).getId();
                    instancia.collection("Recordatorios").document(idDocumento).delete();
                }
            }
        }
    });

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
