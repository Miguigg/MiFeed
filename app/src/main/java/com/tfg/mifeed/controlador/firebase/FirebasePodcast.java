package com.tfg.mifeed.controlador.firebase;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.tfg.mifeed.R;
import com.tfg.mifeed.controlador.activities.Activities.Podcast.CreacionRecordatorioActivity;
import com.tfg.mifeed.controlador.activities.Activities.Podcast.FragmentsPodcast.BibliotecaFragment;
import com.tfg.mifeed.controlador.activities.Activities.Podcast.FragmentsPodcast.MasTardeFragment;
import com.tfg.mifeed.controlador.activities.Activities.Podcast.FragmentsPodcast.RecordatoriosFragment;
import com.tfg.mifeed.controlador.utilidades.Validaciones;
import com.tfg.mifeed.modelo.Episodio;
import com.tfg.mifeed.modelo.Podcast;
import com.tfg.mifeed.modelo.Recordatorio;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class FirebasePodcast {

  private static FirebaseFirestore instancia;

  private static FirebaseAuth userAuth;

  public FirebasePodcast() {
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

  public static void addParaMasTarde(
      Episodio episodio, String titulo, Context c, String idPodcast, String tituloPodcast) {
    String idUsuario = userAuth.getCurrentUser().getUid();
    Map<String, Object> episodioMasTarde = new HashMap<>();
    episodioMasTarde.put("usuario", idUsuario);
    episodioMasTarde.put("nombreCapitulo", titulo);
    episodioMasTarde.put("urlImagen", episodio.getImage());
    episodioMasTarde.put("urlAudio", episodio.getAudio());
    episodioMasTarde.put("idPodcast", idPodcast);
    episodioMasTarde.put("nombrePodcast", tituloPodcast);
    episodioMasTarde.put("descripcion", tituloPodcast);
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
                    String tituloPodcast =
                        (String) queryDocumentSnapshots.getDocuments().get(i).get("nombrePodcast");
                    Episodio episodio =
                        new Episodio(
                            nombreCapitulo,
                            urlImagen,
                            urlAudio,
                            idPodcast,
                            descripcion,
                            tituloPodcast);
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

  public static void getPodcastMasTarde() {
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
                    String tituloPodcast =
                        (String) queryDocumentSnapshots.getDocuments().get(i).get("nombrePodcast");
                    Episodio episodio =
                        new Episodio(
                            nombreCapitulo,
                            urlImagen,
                            urlAudio,
                            idPodcast,
                            descripcion,
                            tituloPodcast);
                    toret.add(episodio);
                  }
                }
                if (toret.size() > 0) {
                  masTardeFragment.respuestaListaPodcast(toret, "true");
                } else {
                  masTardeFragment.respuestaListaPodcast(toret, "false");
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
                  if (queryDocumentSnapshots.getDocuments().get(i).get("creador").equals(id)
                      && queryDocumentSnapshots
                          .getDocuments()
                          .get(i)
                          .get("idPodcast")
                          .equals(podcast.getId())) {
                    ids.add((String) queryDocumentSnapshots.getDocuments().get(i).get("idPodcast"));
                    Log.d("creador", id);
                    Log.d("creadorRecordatorio", (String) queryDocumentSnapshots.getDocuments().get(i).get("creador"));
                  }
                }
                if (!ids.contains(podcast.getId())) {
                  instancia
                      .collection("Podcast")
                      .document()
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
    ref.get()
        .addOnSuccessListener(
            new OnSuccessListener<QuerySnapshot>() {
              @Override
              public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (int i = 0; i < queryDocumentSnapshots.size(); i++) {
                  if (queryDocumentSnapshots.getDocuments().get(i).get("creador").equals(id)) {
                    String idPodcast =
                        (String) queryDocumentSnapshots.getDocuments().get(i).get("idPodcast");
                    String imagen =
                        (String) queryDocumentSnapshots.getDocuments().get(i).get("imagen");
                    String titulo =
                        (String) queryDocumentSnapshots.getDocuments().get(i).get("titulo");
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
                        .addOnCompleteListener(
                            new OnCompleteListener<Void>() {
                              @Override
                              public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                  Toast.makeText(c, R.string.txtDeletePodcast, Toast.LENGTH_SHORT)
                                      .show();
                                }
                              }
                            });
                  }
                }
              }
            });
  }

  public static void guardarRecordatorio(
      Episodio episodio, String fecha, View v, int numero, String idPodcast) {
    String id = userAuth.getCurrentUser().getUid();
    Map<String, Object> nuevosRecordatorio = new HashMap<>();
    nuevosRecordatorio.put("recordatorio", Validaciones.getDateFromString(fecha));
    nuevosRecordatorio.put("titulo", episodio.getTitle());
    nuevosRecordatorio.put("creador", id);
    nuevosRecordatorio.put("recordatorioNumero", numero);
    nuevosRecordatorio.put("urlAudio", episodio.getAudio());
    nuevosRecordatorio.put("urlImagen", episodio.getImage());
    nuevosRecordatorio.put("idPodcast", idPodcast);

    instancia
        .collection("Recordatorios")
        .document()
        .set(nuevosRecordatorio)
        .addOnCompleteListener(
            new OnCompleteListener<Void>() {
              @Override
              public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                  Toast.makeText(v.getContext(), R.string.recordatorioAñadido, Toast.LENGTH_LONG)
                      .show();
                } else {
                  Toast.makeText(v.getContext(), R.string.errConn, Toast.LENGTH_LONG).show();
                }
              }
            });
  }

  public static void getNumeroRecordatorio(long timestamp, View v) {
    CreacionRecordatorioActivity creacionRecordatorioActivity = new CreacionRecordatorioActivity();
    String id = userAuth.getCurrentUser().getUid();

    CollectionReference ref = instancia.collection("Recordatorios");
    ref.get()
        .addOnSuccessListener(
            new OnSuccessListener<QuerySnapshot>() {
              @Override
              public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                ArrayList<Integer> listaIds = new ArrayList<>();
                for (int i = 0; i < queryDocumentSnapshots.size(); i++) {
                  if (queryDocumentSnapshots.getDocuments().get(i).get("creador").equals(id)) {
                    listaIds.add(
                        Math.toIntExact(
                            (Long)
                                queryDocumentSnapshots
                                    .getDocuments()
                                    .get(i)
                                    .get("recordatorioNumero")));
                  }
                }
                if (listaIds.size() > 0) {
                  Log.d("tamaño", String.valueOf(listaIds.size()));
                  int max = 0;
                  for (int i = 0; i < listaIds.size(); i++) {
                    if (max < listaIds.get(i)) {
                      max = listaIds.get(i);
                    }
                  }
                  creacionRecordatorioActivity.setAlarma(max, timestamp, v);
                } else {
                  creacionRecordatorioActivity.setAlarma(0, timestamp, v);
                }
              }
            });
  }

  public static void setRecordatorio(Episodio episodio, String fecha, View v, String idPodcast) {
    String id = userAuth.getCurrentUser().getUid();
    CollectionReference ref = instancia.collection("Recordatorios");
    ArrayList<String> listaAudios = new ArrayList<>();
    ref.get()
        .addOnSuccessListener(
            new OnSuccessListener<QuerySnapshot>() {
              @Override
              public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (int i = 0; i < queryDocumentSnapshots.size(); i++) {
                  if (queryDocumentSnapshots.getDocuments().get(i).get("creador").equals(id)) {
                    listaAudios.add(
                        (String) queryDocumentSnapshots.getDocuments().get(i).get("urlAudio"));
                  }
                }
                if (!listaAudios.contains(episodio.getAudio())) {
                  ref.get()
                      .addOnSuccessListener(
                          new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                              ArrayList<Integer> listaIds = new ArrayList<>();
                              for (int i = 0; i < queryDocumentSnapshots.size(); i++) {
                                if (queryDocumentSnapshots
                                    .getDocuments()
                                    .get(i)
                                    .get("creador")
                                    .equals(id)) {
                                  listaIds.add(
                                      Math.toIntExact(
                                          (Long)
                                              queryDocumentSnapshots
                                                  .getDocuments()
                                                  .get(i)
                                                  .get("recordatorioNumero")));
                                }
                              }
                              if (listaIds.size() > 0) {
                                int max = listaIds.get(0);
                                for (int i = 1; i < listaIds.size(); i++) {
                                  if (max < listaIds.get(i)) {
                                    max = listaIds.get(i);
                                  }
                                }
                                max = max + 1;
                                guardarRecordatorio(episodio, fecha, v, max, idPodcast);
                              } else {
                                guardarRecordatorio(episodio, fecha, v, 1, idPodcast);
                              }
                            }
                          });
                } else {
                  Toast.makeText(v.getContext(), R.string.errPodcastYaExiste, Toast.LENGTH_SHORT)
                      .show();
                }
              }
            });
  }

  public static void getRecordatorios(View v) {
    RecordatoriosFragment recordatoriosFragment = new RecordatoriosFragment();
    String id = userAuth.getCurrentUser().getUid();
    CollectionReference ref = instancia.collection("Recordatorios");
    ArrayList<Recordatorio> toret = new ArrayList<>();
    ref.get()
        .addOnSuccessListener(
            new OnSuccessListener<QuerySnapshot>() {
              @Override
              public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (int i = 0; i < queryDocumentSnapshots.size(); i++) {
                  if (queryDocumentSnapshots.getDocuments().get(i).get("creador").equals(id)) {
                    String titulo =
                        (String) queryDocumentSnapshots.getDocuments().get(i).get("titulo");
                    String urlAudio =
                        (String) queryDocumentSnapshots.getDocuments().get(i).get("urlAudio");
                    String urlImagen =
                        (String) queryDocumentSnapshots.getDocuments().get(i).get("urlImagen");
                    String idPodcast =
                        (String) queryDocumentSnapshots.getDocuments().get(i).get("idPodcast");
                    int idRecordatorio =
                        Math.toIntExact(
                            (Long)
                                queryDocumentSnapshots
                                    .getDocuments()
                                    .get(i)
                                    .get("recordatorioNumero"));
                    Timestamp timestamp =
                        (Timestamp)
                            queryDocumentSnapshots.getDocuments().get(i).get("recordatorio");
                    Date fecha = timestamp.toDate();

                    Recordatorio recordatorio =
                        new Recordatorio(
                            fecha, idRecordatorio, titulo, urlAudio, urlImagen, idPodcast);
                    toret.add(recordatorio);
                  }
                }
                if (toret.size() > 0) {
                  recordatoriosFragment.respuestaListaRecordatorios(toret, "true", v);
                } else {
                  recordatoriosFragment.respuestaListaRecordatorios(toret, "false", v);
                }
              }
            });
  }

  public static void eliminarRecordatorio(String idPodcast, Context c) {
    String id = userAuth.getCurrentUser().getUid();
    CollectionReference ref = instancia.collection("Recordatorios");
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
                    String idDocumento = queryDocumentSnapshots.getDocuments().get(i).getId();
                    instancia
                        .collection("Recordatorios")
                        .document(idDocumento)
                        .delete()
                        .addOnSuccessListener(
                            new OnSuccessListener<Void>() {
                              @Override
                              public void onSuccess(Void unused) {
                                Toast.makeText(c, R.string.txtDeletePodcast, Toast.LENGTH_LONG)
                                    .show();
                              }
                            });
                  }
                }
              }
            });
  }

  public static void checkRecordatorios() {
    CreacionRecordatorioActivity creacionRecordatorioActivity = new CreacionRecordatorioActivity();
    String id = userAuth.getCurrentUser().getUid();
    DocumentReference ref = instancia.collection("Users").document(id);
    ref.get()
        .addOnSuccessListener(
            new OnSuccessListener<DocumentSnapshot>() {
              @Override
              public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.get("notificaciones").equals("true")) {
                  creacionRecordatorioActivity.respuestaRecordatorios("true");
                } else {
                  creacionRecordatorioActivity.respuestaRecordatorios("false");
                }
              }
            });
  }
}
