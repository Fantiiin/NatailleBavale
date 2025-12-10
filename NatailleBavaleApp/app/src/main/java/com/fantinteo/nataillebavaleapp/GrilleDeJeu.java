// Dans le fichier GrilleDeJeu.java
package com.votrenom.bataillenavale.model;

import java.util.ArrayList;
import java.util.List;

public class GrilleDeJeu {
    private final int largeur;
    private final int hauteur;
    private final java.util.List<Navire> navires;

    public GrilleDeJeu(int largeur, int hauteur) {
        // Assure une taille minimale de 10x10 comme demandé
        this.largeur = Math.max(10, largeur);
        this.hauteur = Math.max(10, hauteur);
        this.navires = new ArrayList<>();
    }

    public int getLargeur() {
        return largeur;
    }

    public int getHauteur() {
        return hauteur;
    }

    public java.util.List<Navire> getNavires() {
        return navires;
    }

    /**
     * Tente de placer un navire sur la grille.
     * Le navire n'est ajouté que si le placement est valide.
     * @param navire Le navire à placer.
     * @return true si le navire a été placé avec succès, false sinon.
     */
    public boolean placerNavire(Navire navire) {
        if (placementEstValide(navire)) {
            navires.add(navire);
            return true;
        }
        return false;
    }

    /**
     * Vérifie si un navire peut être placé sur la grille.
     * Un placement est valide si :
     * 1. Tous ses éléments sont dans les limites de la grille.
     * 2. Il ne chevauche aucun autre navire.
     * 3. Il n'est en contact avec aucun autre navire (règle de la case d'écart).
     */
    private boolean placementEstValide(Navire nouveauNavire) {
        for (ElementNavire nouvelElement : nouveauNavire.getElements()) {
            int x = nouvelElement.getCoordonnee().getX();
            int y = nouvelElement.getCoordonnee().getY();

            // 1. Vérifier si l'élément est dans la grille
            if (x < 0 || x >= largeur || y < 0 || y >= hauteur) {
                return false; // Hors des limites
            }

            // 2. Vérifier la proximité avec les navires déjà placés
            for (Navire navireExistant : navires) {
                for (ElementNavire elementExistant : navireExistant.getElements()) {
                    int existantX = elementExistant.getCoordonnee().getX();
                    int existantY = elementExistant.getCoordonnee().getY();

                    // Vérifie une zone de 3x3 autour de chaque élément existant.
                    // Si le nouvel élément est dans cette zone, le placement est invalide.
                    if (Math.abs(x - existantX) <= 1 && Math.abs(y - existantY) <= 1) {
                        return false; // Trop proche ou en contact
                    }
                }
            }
        }
        return true; // Toutes les vérifications sont passées
    }
}
