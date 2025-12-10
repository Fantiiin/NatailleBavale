package com.fantinteo.nataillebavaleapp.model;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Random;

public class SimpleAI {
    private final Random random = new Random();
    private final int rows;
    private final int cols;

    // Liste des coordonnées non ciblées (Mode CHASSE)
    private final List<int[]> availableTargets;

    // File d'attente des coordonnées adjacentes à essayer (Mode CIBLAGE)
    private final Queue<int[]> targetsToPursue;

    // Stocke toutes les cases déjà ciblées (pour éviter de tirer deux fois)
    private final boolean[][] alreadyTargeted;

    // Directions possibles : Haut, Bas, Gauche, Droite (dx, dy)
    private static final int[][] DIRECTIONS = {
            {0, -1}, // Haut
            {0, 1},  // Bas
            {-1, 0}, // Gauche
            {1, 0}   // Droite
    };

    public SimpleAI(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
        this.availableTargets = new ArrayList<>();
        this.targetsToPursue = new LinkedList<>();
        this.alreadyTargeted = new boolean[rows][cols]; // [y][x]

        initializeTargets();
    }

    private void initializeTargets() {
        for (int y = 0; y < rows; y++) {
            for (int x = 0; x < cols; x++) {
                // Initialiser la liste de chasse
                availableTargets.add(new int[]{x, y});
            }
        }
    }

    /**
     * Détermine la prochaine cible. Priorise le ciblage (autour d'un touché) puis la chasse (aléatoire).
     * @return un tableau int[2] contenant {x, y} ou null.
     */
    public int[] getNextShot() {
        if (!targetsToPursue.isEmpty()) {
            // Mode CIBLAGE : On tire sur la prochaine cible adjacente
            return targetsToPursue.poll();
        }

        // Mode CHASSE : On tire aléatoirement
        if (availableTargets.isEmpty()) {
            return null; // Plus de cibles disponibles
        }

        int index = random.nextInt(availableTargets.size());
        int[] target = availableTargets.remove(index);

        // On marque la case comme ciblée avant de la retourner
        markTargeted(target[0], target[1]);
        return target;
    }

    /**
     * Met à jour l'IA après un tir réussi (HIT) ou le coup de grâce (SUNK).
     * @param x Coordonnée X
     * @param y Coordonnée Y
     * @param result Le résultat du tir (1=Touché, 2=Coulé, 0=Raté)
     */
    public void updateStatus(int x, int y, int result) {
        if (result == 1) { // TOUCHÉ (HIT)
            // L'IA doit passer en mode ciblage. Ajout des cases adjacentes à explorer.
            addAdjacentTargets(x, y);
        } else if (result == 2) { // COULÉ (SUNK)
            // Réinitialisation du mode ciblage si des cibles étaient encore en attente,
            // car le navire est détruit, il ne sert à rien de continuer à chercher autour.
            // Cependant, on ne vide que les cibles qui pourraient être des restes du navire coulé.
            // Pour l'IA simple, on vide simplement la file, forçant un retour à la chasse.
            targetsToPursue.clear();
        }
        // Pour Raté (0) et Coup invalide (-1), aucune action de ciblage n'est nécessaire.
    }

    /**
     * Ajoute les quatre cases adjacentes non ciblées à la file d'attente de ciblage.
     */
    private void addAdjacentTargets(int x, int y) {
        for (int[] direction : DIRECTIONS) {
            int nx = x + direction[0];
            int ny = y + direction[1];

            // 1. Vérifier les limites de la grille
            if (nx >= 0 && nx < cols && ny >= 0 && ny < rows) {
                // 2. Vérifier si la case n'a pas déjà été ciblée/tirée
                if (!alreadyTargeted[ny][nx]) {
                    int[] target = new int[]{nx, ny};

                    // Marquer immédiatement comme ciblée pour ne pas l'ajouter deux fois
                    markTargeted(nx, ny);

                    // Retirer de la liste de chasse aléatoire (availableTargets)
                    removeTargetFromAvailable(nx, ny);

                    // Ajouter à la file de ciblage
                    targetsToPursue.offer(target);
                }
            }
        }
    }

    /**
     * Marque une coordonnée comme ayant été ciblée (pour éviter les doublons).
     */
    private void markTargeted(int x, int y) {
        alreadyTargeted[y][x] = true;
    }

    /**
     * Retire une cible de la liste de chasse pour éviter qu'elle soit choisie aléatoirement
     * après avoir été ajoutée au ciblage.
     */
    private void removeTargetFromAvailable(int x, int y) {
        // Cette opération est coûteuse (O(n)), mais garantit l'intégrité des listes.
        availableTargets.removeIf(target -> target[0] == x && target[1] == y);
    }

    // Utilisé par GameActivity pour marquer un tir (HIT ou MISS) sur la grille du joueur.
    // L'IA utilise déjà 'alreadyTargeted' en interne, cette méthode est un double contrôle
    // si l'IA tirait hors de 'getNextShot' (non pertinent ici).
    public boolean isTargeted(int x, int y) {
        return alreadyTargeted[y][x];
    }
}