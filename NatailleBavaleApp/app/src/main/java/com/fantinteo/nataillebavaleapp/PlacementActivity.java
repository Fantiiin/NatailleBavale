package com.fantinteo.nataillebavaleapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.fantinteo.nataillebavaleapp.adapter.GridAdapter;
import com.fantinteo.nataillebavaleapp.model.Cruiser;
import com.fantinteo.nataillebavaleapp.model.Escort;
import com.fantinteo.nataillebavaleapp.model.Grid;
import com.fantinteo.nataillebavaleapp.model.Ship;
import com.fantinteo.nataillebavaleapp.model.Submarine;

import java.util.LinkedList;
import java.util.Queue;

public class PlacementActivity extends Activity {

    private Grid playerGrid;
    private GridAdapter placementAdapter;
    private TextView statusTextView;
    private Button nextShipButton;
    private Switch orientationSwitch;

    private Queue<Integer> shipsToPlaceSizes;
    private int currentShipSize;
    private int startX = -1, startY = -1;

    private static final int GRID_SIZE = 10;
    public static final String EXTRA_PLAYER_GRID = "player_grid";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_placement);

        statusTextView = findViewById(R.id.tv_placement_status);
        nextShipButton = findViewById(R.id.btn_next_ship);
        orientationSwitch = findViewById(R.id.switch_orientation);
        GridView placementGridView = findViewById(R.id.placement_grid_view);

        playerGrid = new Grid(GRID_SIZE, GRID_SIZE);
        placementGridView.setNumColumns(GRID_SIZE);

        placementAdapter = new GridAdapter(this, playerGrid, true);
        placementGridView.setAdapter(placementAdapter);

        int[] sizes = getIntent().getIntArrayExtra(MainActivity.EXTRA_SHIPS_TO_PLACE);
        shipsToPlaceSizes = new LinkedList<>();
        if (sizes != null) {
            for (int size : sizes) {
                shipsToPlaceSizes.add(size);
            }
        }

        startNextPlacement();

        placementGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int x = position % GRID_SIZE;
                int y = position / GRID_SIZE;

                handlePlacementAttempt(x, y);
            }
        });

        nextShipButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finalizeShipPlacement();
            }
        });
    }

    private void startNextPlacement() {
        if (shipsToPlaceSizes.isEmpty()) {
            launchGameActivity();
            return;
        }

        currentShipSize = shipsToPlaceSizes.poll();

        String shipName = getShipName(currentShipSize);
        statusTextView.setText("Placez votre " + shipName + " (" + currentShipSize + " cases).");

        startX = -1;
        nextShipButton.setEnabled(false);
    }

    private void handlePlacementAttempt(int x, int y) {
        startX = x;
        startY = y;
        boolean isVertical = orientationSwitch.isChecked();

        Ship tempShip = createShipObject(currentShipSize, null);

        if (playerGrid.isPlacementValid(tempShip, startX, startY, isVertical)) {
            nextShipButton.setEnabled(true);
            Toast.makeText(this, "Placement possible. Cliquez sur VALIDER.", Toast.LENGTH_SHORT).show();
        } else {
            nextShipButton.setEnabled(false);
            Toast.makeText(this, "Placement invalide (hors limites ou contact).", Toast.LENGTH_SHORT).show();
        }
    }

    private void finalizeShipPlacement() {
        if (startX == -1) {
            Toast.makeText(this, "Veuillez cliquer sur une position de départ.", Toast.LENGTH_SHORT).show();
            return;
        }

        boolean isVertical = orientationSwitch.isChecked();

        // La méthode placeShip va recréer l'objet Ship avec les ShipElement
        Ship finalShip = createShipObject(currentShipSize, null);

        if (playerGrid.placeShip(finalShip, startX, startY, isVertical)) {
            placementAdapter.notifyDataSetChanged();
            Toast.makeText(this, getShipName(currentShipSize) + " placé !", Toast.LENGTH_SHORT).show();
            startNextPlacement();
        } else {
            Toast.makeText(this, "Erreur de validation. Veuillez réessayer.", Toast.LENGTH_SHORT).show();
        }
    }

    private void launchGameActivity() {
        Intent intent = new Intent(PlacementActivity.this, GameActivity.class);
        // Transmettre la grille du joueur sérialisée
        intent.putExtra(EXTRA_PLAYER_GRID, playerGrid);
        startActivity(intent);
        finish();
    }

    private String getShipName(int size) {
        if (size == 3) return "Croiseur";
        if (size == 2) return "Escorteur";
        if (size == 1) return "Sous-marin";
        return "Navire Inconnu";
    }

    private Ship createShipObject(int size, Ship.ShipElement element) {
        if (size == 3) return new Cruiser(null);
        if (size == 2) return new Escort(null);
        if (size == 1) return new Submarine(element);
        return null;
    }
}