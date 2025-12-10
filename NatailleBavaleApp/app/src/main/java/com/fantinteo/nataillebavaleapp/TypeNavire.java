// Dans le fichier TypeNavire.java
package com.votrenom.bataillenavale.model;

public enum TypeNavire {
    CROISEUR(3, "Croiseur"),
    ESCORTEUR(2, "Escorteur"),
    SOUS_MARIN(1, "Sous-marin");

    private final int taille;
    private final java.lang.String nomNavire;

    TypeNavire(int taille, java.lang.String nomNavire) {
        this.taille = taille;
        this.nomNavire = nomNavire;
    }

    public int getTaille() {
        return taille;
    }

    public java.lang.String getNomNavire() {
        return nomNavire;
    }
}
