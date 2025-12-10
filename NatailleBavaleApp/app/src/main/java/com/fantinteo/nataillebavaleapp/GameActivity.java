package com.fantinteo.nataillebavaleapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.fantinteo.nataillebavaleapp.adapter.GridAdapter;
import com.fantinteo.nataillebavaleapp.model.Cruiser;
import com.fantinteo.nataillebavaleapp.model.Escort;
import com.fantinteo.nataillebavaleapp.model.Grid;
import com.fantinteo.nataillebavaleapp.model.Ship;
import com.fantinteo.nataillebavaleapp.model.SimpleAI;
import com.fantinteo.nataillebavaleapp.model.Submarine;

import java.util.LinkedList;
import java.util.Queue;

public class GameActivity extends Activity {

    private Grid playerGrid;
    private Grid opponentGrid;
    private SimpleAI simpleAI;

    private GridAdapter playerAdapter;
    private GridAdapter opponentAdapter;
    private TextView statusTextView;
    private final Handler handler = new Handler();

    private boolean isPlayerTurn = true;
    private final int GRID_SIZE = 10;

    // Définition des navires que l'IA doit placer (identique au joueur)
    private final int[] AIShips = new int[]{3, 2, 1};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        statusTextView = findViewById(R.id.tv_status);

        // Récupérer la grille du joueur placée depuis PlacementActivity
        playerGrid = (Grid) getIntent().getSerializableExtra(PlacementActivity.EXTRA_PLAYER_GRID);

        // Initialiser la grille de l'adversaire et l'IA
        opponentGrid = new Grid(GRID_SIZE, GRID_SIZE);
        simpleAI = new SimpleAI(GRID_SIZE, GRID_SIZE);

        // L'IA place ses navires de manière aléatoire
        placeAIShips(opponentGrid);

        // Grille Joueur (Défense - vos navires sont visibles)
        GridView playerGridView = findViewById(R.id.player_grid_view);
        playerGridView.setNumColumns(GRID_SIZE);
        playerAdapter = new GridAdapter(this, playerGrid, true);
        playerGridView.setAdapter(playerAdapter);

        // Grille Adversaire (Attaque - navires cachés)
        GridView opponentGridView = findViewById(R.id.opponent_grid_view);
        opponentGridView.setNumColumns(GRID_SIZE);
        opponentAdapter = new GridAdapter(this, opponentGrid, false);
        opponentGridView.setAdapter(opponentAdapter);

        opponentGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (isPlayerTurn) {
                    int x = position % opponentGrid.getCols();
                    int y = position / opponentGrid.getCols();
                    handlePlayerShot(x, y);
                } else {
                    Toast.makeText(GameActivity.this, "Veuillez attendre le tour de l'IA.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        updateStatus("Bataille ! Tirez sur la grille adverse.");
    }

    private void handlePlayerShot(int x, int y) {
        int result = opponentGrid.fireShot(x, y);
        opponentAdapter.notifyDataSetChanged();

        String message;
        if (result == -1) {
            updateStatus("Coup invalide. Réessayez.");
            return;
        }

        if (result == 1) {
            message = "TOUCHÉ ! Vous rejouez.";
            updateStatus(message);
        } else if (result == 2) {
            message = "COULÉ ! Vous rejouez.";
            updateStatus(message);
            if (opponentGrid.allShipsSunk()) {
                endGame("VICTOIRE !");
                return;
            }
        } else { // Raté
            message = "RATÉ ! Au tour de l'IA.";
            updateStatus(message);
            isPlayerTurn = false;
            handler.postDelayed(this::handleAIShot, 1500);
        }
    }

    private void handleAIShot() {
        int[] target = simpleAI.getNextShot();
        if (target == null) return;

        int x = target[0];
        int y = target[1];

        int result = playerGrid.fireShot(x, y);
        playerAdapter.notifyDataSetChanged();

        simpleAI.updateStatus(x, y, result);

        String message;
        if (result == 1) { // TOUCHÉ
            message = "L'IA vous a TOUCHÉ en (" + (x+1) + "," + (y+1) + ") !";
            updateStatus(message);
            // L'IA rejoue
            handler.postDelayed(this::handleAIShot, 1500);
        } else if (result == 2) { // COULÉ
            message = "L'IA a COULÉ un de vos navires !";
            updateStatus(message);
            if (playerGrid.allShipsSunk()) {
                endGame("DÉFAITE...");
                return;
            }
            // L'IA rejoue (car un navire coulé ne termine pas son tour)
            handler.postDelayed(this::handleAIShot, 1500);
        } else { // Raté
            message = "L'IA a RATÉ en (" + (x+1) + "," + (y+1) + "). Votre tour.";
            updateStatus(message);
            isPlayerTurn = true;
        }
    }

    private void placeAIShips(Grid grid) {
        java.util.Random rand = new java.util.Random();
        for (int size : AIShips) {
            Ship shipToPlace;
            if (size == 3) shipToPlace = new Cruiser(null);
            else if (size == 2) shipToPlace = new Escort(null);
            else shipToPlace = new Submarine(null);

            boolean placed = false;
            while (!placed) {
                int startX = rand.nextInt(GRID_SIZE);
                int startY = rand.nextInt(GRID_SIZE);
                boolean isVertical = rand.nextBoolean();

                placed = grid.placeShip(shipToPlace, startX, startY, isVertical);
            }
        }
    }

    private void updateStatus(String message) {
        statusTextView.setText(message);
    }

    private void endGame(String message) {
        updateStatus(message);

        // 1. Désactiver le tir
        GridView opponentGridView = findViewById(R.id.opponent_grid_view);
        opponentGridView.setOnItemClickListener(null);

        // 2. Afficher la boîte de dialogue de fin de partie
        new AlertDialog.Builder(this)
                .setTitle("Partie Terminée")
                .setMessage(message + "\nVoulez-vous retourner au menu principal ?")
                .setPositiveButton("Retour au Menu", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Crée un Intent pour retourner à MainActivity
                        Intent intent = new Intent(GameActivity.this, MainActivity.class);
                        // Ces drapeaux s'assurent que toutes les activités précédentes (Placement, Game) sont fermées
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    }
                })
                .setCancelable(false) // Empêche la fermeture par clic extérieur ou bouton retour
                .show();
    }
}