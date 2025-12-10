// Dans le fichier Navire.java
package com.votrenom.bataillenavale.model;

import java.util.List;

public class Navire {
    private final TypeNavire type;
    private final java.util.List<ElementNavire> elements;

    public Navire(TypeNavire type, java.util.List<ElementNavire> elements) {
        this.type = type;
        this.elements = elements;
    }

    public TypeNavire getType() {
        return type;
    }

    public java.util.List<ElementNavire> getElements() {
        return elements;
    }

    /**
     * Vérifie si le navire est entièrement coulé.
     * @return true si tous ses éléments sont touchés, false sinon.
     */
    public boolean estCoule() {
        for (ElementNavire element : elements) {
            if (element.getEtat() == EtatElement.SAIN) {
                return false; // Si on trouve un seul élément sain, le navire n'est pas coulé
            }
        }
        return true; // Tous les éléments ont été vérifiés et sont touchés
    }
}
