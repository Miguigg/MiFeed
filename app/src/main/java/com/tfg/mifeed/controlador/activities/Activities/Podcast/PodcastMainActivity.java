package com.tfg.mifeed.controlador.activities.Activities.Podcast;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.tfg.mifeed.R;
import com.tfg.mifeed.controlador.activities.Activities.AppActivity;
import com.tfg.mifeed.controlador.activities.Activities.Podcast.FragmentsPodcast.BibliotecaFragment;
import com.tfg.mifeed.controlador.activities.Activities.Podcast.FragmentsPodcast.BusquedaFragment;
import com.tfg.mifeed.controlador.activities.Activities.Podcast.FragmentsPodcast.MasTardeFragment;
import com.tfg.mifeed.controlador.activities.Activities.Podcast.FragmentsPodcast.RecordatoriosFragment;
import com.tfg.mifeed.controlador.utilidades.CheckConexion;
import com.tfg.mifeed.controlador.utilidades.Validaciones;

public class PodcastMainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationMenuView;
    private BusquedaFragment busquedaFragment;
    private BibliotecaFragment bibliotecaFragment;
    private MasTardeFragment masTardeFragment;
    private RecordatoriosFragment recordatoriosFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_podcast_main);
        busquedaFragment = new BusquedaFragment();
        bibliotecaFragment = new BibliotecaFragment();
        masTardeFragment = new MasTardeFragment();
        recordatoriosFragment = new RecordatoriosFragment();
        bottomNavigationMenuView = findViewById(R.id.bottomNavigationView);

        final AppActivity app = (AppActivity) this.getApplication();
        app.generarBarraInferior(this.findViewById(android.R.id.content),this);
        getSupportFragmentManager().beginTransaction().replace(R.id.framePodcast,busquedaFragment).commit();
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if(extras != null && extras.containsKey("alarma")){
            getSupportFragmentManager().beginTransaction().replace(R.id.framePodcast,recordatoriosFragment).commit();
        }
        bottomNavigationMenuView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if(!CheckConexion.getEstadoActual(PodcastMainActivity.this)){
                    Toast.makeText(PodcastMainActivity.this,R.string.errConn,Toast.LENGTH_LONG).show();
                }else{
                    switch (item.getItemId()){
                        case R.id.frag_busqueda:
                            getSupportFragmentManager().beginTransaction().replace(R.id.framePodcast,busquedaFragment).commit();
                            return true;
                        case R.id.frag_biblio:
                            getSupportFragmentManager().beginTransaction().replace(R.id.framePodcast,bibliotecaFragment).commit();
                            return true;
                        case R.id.frag_masTarde:
                            getSupportFragmentManager().beginTransaction().replace(R.id.framePodcast,masTardeFragment).commit();
                            return true;
                        case R.id.frag_recordatorios:
                            getSupportFragmentManager().beginTransaction().replace(R.id.framePodcast,recordatoriosFragment).commit();
                            return true;
                    }
                }
                return false;
            }
        });
    }
    public void onBackPressed() {
        Validaciones.confirmarExit(PodcastMainActivity.this);
    }
}