package com.tfg.mifeed.controlador.firebase;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.tfg.mifeed.R;
import com.tfg.mifeed.controlador.activities.Activities.Prensa.FragmentsPrensa.EtiquetasFragment;
import com.tfg.mifeed.controlador.activities.Activities.Prensa.HistorialActivity;
import com.tfg.mifeed.controlador.activities.Activities.Prensa.NoticiaActivity;
import com.tfg.mifeed.modelo.Etiqueta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class FirebaseNoticias {

  private static FirebaseFirestore instancia;

  private static FirebaseAuth userAuth;

  public FirebaseNoticias() {
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

  public static void crearEtiqueta(String nombre, View v) {
      /*
      * Recibe el nombre de la nueva etiqueta y la crea en firebase
      * */
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
      /*
      * Teniendo el nombre de la etiqueta, procede a eliminarla en firebase (solo si el creador coincide)
      * */
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

  public static void getNombresEtiquetas(LayoutInflater inf) {
      /*
      * Obtiene de firerbase los nombres de todas las etiquetas, del usuario
      * */
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
                  noticiaActivity.respuestaNombresEtiquetas("true", nombreEtiquetas, inf);
                } else {
                  noticiaActivity.respuestaNombresEtiquetas("false", nombreEtiquetas, inf);
                }
              }
            });
  }

  public static void getEtiquetas(View v) {
      /*
      * Obtiene de firebase todas las etiquetas del usuario, con su contenido
      * */
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

  public static void eliminarUrlLista(String url, String nombreEtiqueta) {
      /*
      * Dada la url y el nombre de la etiqueta, borra el enlace de la etiqueta en firebase
      * */
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
      /*
      * recibe la nueva lista de urls y titulos y los actualiza en firebase
      * */
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
      /*
      * Recibiendo la url y el nombre, los añade a las listas de la etiqueta indicada
      * */
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

  public static void getHistorial() {
      /*
      * Obtiene el historial de articulos visitados por el usuario logeado
      * */
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
                historialActivity.respuestaHistorial(historial);
              }
            });
  }

  public static void insertarHistorial(String url) {
      /*
      * Inserta en el historial del usuario una vez visita la pagina
      * */
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
      /*
      * Elimina la url indicada del historial
      * */
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
      /*
      * Elimina todo el historial del usuario
      * */
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
