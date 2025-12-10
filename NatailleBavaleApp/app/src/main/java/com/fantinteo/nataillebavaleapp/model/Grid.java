package com.fantinteo.nataillebavaleapp.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Grid implements Serializable {
    private final int rows;
    private final int cols;
    private final List<Ship> ships;

    // 0=Eau, 1=Navire, 2=Touché (Hit), 3=Raté (Miss), 4=Coulé (Sunk)
    private int[][] gridState;

    public Grid(int rows, int cols) {
        this.rows = Math.max(rows, 10);
        this.cols = Math.max(cols, 10);
        this.ships = new ArrayList<>();
        this.gridState = new int[this.rows][this.cols];
    }

    public boolean placeShip(Ship newShip, int startX, int startY, boolean isVertical) {
        List<Ship.ShipElement> potentialElements = new ArrayList<>();
        int length = newShip.getSize();

        // 1. Définir les éléments potentiels et vérifier les limites
        for (int i = 0; i < length; i++) {
            int x = isVertical ? startX : startX + i;
            int y = isVertical ? startY + i : startY;
            potentialElements.add(new Ship.ShipElement(x, y));
        }

        // 2. Vérification complète (limites, contact et superposition)
        if (!isPlacementValid(newShip, startX, startY, isVertical)) {
            return false;
        }

        // 3. Placement final
        for (Ship.ShipElement element : potentialElements) {
            gridState[element.getY()][element.getX()] = 1;
        }

        // Assigner les éléments au navire et l'ajouter
        if (newShip instanceof Cruiser) {
            ships.add(new Cruiser(potentialElements));
        } else if (newShip instanceof Escort) {
            ships.add(new Escort(potentialElements));
        } else if (newShip instanceof Submarine) {
            ships.add(new Submarine(potentialElements.get(0)));
        }

        return true;
    }

    public boolean isPlacementValid(Ship newShip, int startX, int startY, boolean isVertical) {
        List<Ship.ShipElement> potentialElements = new ArrayList<>();
        int length = newShip.getSize();

        for (int i = 0; i < length; i++) {
            int x = isVertical ? startX : startX + i;
            int y = isVertical ? startY + i : startY;

            if (x < 0 || x >= cols || y < 0 || y >= rows) {
                return false; // Hors limites
            }
            potentialElements.add(new Ship.ShipElement(x, y));
        }

        // Vérifier les contacts (une case de séparation requise) et la superposition
        for (Ship.ShipElement element : potentialElements) {
            int x = element.getX();
            int y = element.getY();

            // Vérifier la superposition sur la case elle-même
            if (gridState[y][x] == 1) {
                return false;
            }

            // Vérifier les 8 voisins (contact)
            for (int dx = -1; dx <= 1; dx++) {
                for (int dy = -1; dy <= 1; dy++) {
                    int nx = x + dx;
                    int ny = y + dy;

                    if (nx >= 0 && nx < cols && ny >= 0 && ny < rows) {
                        // Si une case voisine est déjà occupée par un navire
                        if (gridState[ny][nx] == 1) {
                            // S'assurer que le voisin n'est pas un élément du navire que l'on est en train de placer.
                            // Le "gridState[ny][nx] == 1" garantit qu'il s'agit d'un navire déjà placé.
                            // La vérification ci-dessous n'est pas nécessaire si gridState[y][x] est géré correctement,
                            // mais on garde la logique de contact simple pour la sécurité.
                            return true; // Contact détecté avec un navire existant
                        }
                    }
                }
            }
        }
        return true;
    }

    public int fireShot(int x, int y) {
        if (x < 0 || x >= cols || y < 0 || y >= rows || gridState[y][x] > 1) {
            return -1; // Coup invalide ou déjà tiré
        }

        if (gridState[y][x] == 1) { // Navire
            gridState[y][x] = 2; // Touché

            for (Ship ship : ships) {
                for (Ship.ShipElement element : ship.elements) {
                    if (element.getX() == x && element.getY() == y) {
                        element.setTouched(true);

                        if (ship.isSunk()) {
                            for (Ship.ShipElement sunkElement : ship.elements) {
                                gridState[sunkElement.getY()][sunkElement.getX()] = 4; // Coulé
                            }
                            return 2; // Coulé
                        }
                        return 1; // Touché
                    }
                }
            }
        }

        gridState[y][x] = 3; // Raté
        return 0; // Raté
    }

    public boolean allShipsSunk() {
        for (Ship ship : ships) {
            if (!ship.isSunk()) {
                return false;
            }
        }
        return true;
    }

    public int getRows() { return rows; }
    public int getCols() { return cols; }
    public int getGridState(int x, int y) {
        if (x >= 0 && x < cols && y >= 0 && y < rows) {
            return gridState[y][x];
        }
        return 0;
    }
}