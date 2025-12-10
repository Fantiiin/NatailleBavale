package com.votrenom.bataillenavale.model;
package com.votrenom.bataillenavale.model;

public enum EtatElement {
    SAIN,
    TOUCHE
}

public class ElementNavire {
    private final Coordonnee coordonnee;
    private EtatElement etat;    public ElementNavire(Coordonnee coordonnee) {
        this.coordonnee = coordonnee;
        this.etat = EtatElement.SAIN; // Par défaut, un élément est "sain"
    }

    public Coordonnee getCoordonnee() {
        return coordonnee;
    }

    public EtatElement getEtat() {
        return etat;
    }

    public void setEtat(EtatElement etat) {
        this.etat = etat;
    }
}
