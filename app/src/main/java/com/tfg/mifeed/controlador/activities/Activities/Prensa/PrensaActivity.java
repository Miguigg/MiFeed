package com.tfg.mifeed.controlador.activities.Activities.Prensa;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.tfg.mifeed.R;
import com.tfg.mifeed.controlador.activities.Activities.AppActivity;
import com.tfg.mifeed.controlador.activities.Activities.Prensa.FragmentsPrensa.CategoriasFragment;
import com.tfg.mifeed.controlador.activities.Activities.Prensa.FragmentsPrensa.EtiquetasFragment;
import com.tfg.mifeed.controlador.activities.Activities.Prensa.FragmentsPrensa.FavoritosFragment;
import com.tfg.mifeed.controlador.activities.Activities.Prensa.FragmentsPrensa.ImportantesFragment;


public class PrensaActivity extends AppCompatActivity {
    BottomNavigationView bottomNavigationMenuView;
    FavoritosFragment favoritosFragment;
    CategoriasFragment categoriasFragment;
    EtiquetasFragment etiquetasFragment;
    ImportantesFragment importantesFragment;

    private static View v;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prensa);
        bottomNavigationMenuView = findViewById(R.id.bottomNavigationView);
        favoritosFragment = new FavoritosFragment();
        categoriasFragment = new CategoriasFragment();
        etiquetasFragment = new EtiquetasFragment();
        importantesFragment = new ImportantesFragment();
        v = this.findViewById(android.R.id.content);
        final AppActivity app = (AppActivity) this.getApplication();
        app.generarBarraInferior(this.findViewById(android.R.id.content),this);
        getSupportFragmentManager().beginTransaction().replace(R.id.framePrensa,favoritosFragment).commit();
        bottomNavigationMenuView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.frag_favotiros:
                        getSupportFragmentManager().beginTransaction().replace(R.id.framePrensa,favoritosFragment).commit();
                        return true;
                    case R.id.frag_categorias:
                        getSupportFragmentManager().beginTransaction().replace(R.id.framePrensa,categoriasFragment).commit();
                        return true;
                    case R.id.frag_importantes:
                        getSupportFragmentManager().beginTransaction().replace(R.id.framePrensa,importantesFragment).commit();
                        return true;
                    case R.id.frag_etiquetas:
                        getSupportFragmentManager().beginTransaction().replace(R.id.framePrensa,etiquetasFragment).commit();
                        return true;
                }
                return false;
            }
        });
    }
}