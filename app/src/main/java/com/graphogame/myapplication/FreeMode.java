package com.graphogame.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;



/**
 * En développement.
 * Vue correspondant au mode libre, accessible par le patient lorsqu'aucune activité n'est à faire. Pour plus de détails, se référer au rapport.
 *
 */
public class FreeMode extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_free_mode);

        /* On efface le nom de l'application de la barre d'action et on ajoute un lien retour vers la page d'acceuil */
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        /* Renvoie vers la page d'acceuil lorsqu'on clique sur le lien */
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FreeMode.this, HomePage.class);
                finish();
                startActivity(intent);
            }
        });
    }


}
