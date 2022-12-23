package com.tfg.mifeed.view;

import android.app.Application;

public class AppActivity extends Application {
  public int prueba;
  @Override
  public void onCreate() {
    super.onCreate();
    prueba = 1;
  }
}
