package com.fantinteo.nataillebavaleapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends Activity {

    public static final String EXTRA_SHIPS_TO_PLACE = "ships_to_place";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnStart = findViewById(R.id.btn_start_game);

        // Définition des navires à placer : Croiseur(3), Escorteur(2), Sous-marin(1)
        final int[] defaultShips = new int[]{3, 2, 1};

        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, PlacementActivity.class);
                intent.putExtra(EXTRA_SHIPS_TO_PLACE, defaultShips);
                startActivity(intent);
            }
        });
    }
}