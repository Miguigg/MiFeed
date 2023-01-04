package com.tfg.mifeed.controlador.activities.Activities;

import android.app.Application;

public class AppActivity extends Application {
  /*
  * Clase que sustituye a la clase Application por defecto y que será accesible por el código de toda la aplicación.
  * En ella se han introducido aquellas funciones relacionadas con la generación de la funcionalidad de las barras inferior y superior
  *
  * */
  @Override
  public void onCreate() {
    super.onCreate();
  }

  public String generarBarraInferior(){
    return "Prueba de conexion con mi nueva Application";
  }
}
