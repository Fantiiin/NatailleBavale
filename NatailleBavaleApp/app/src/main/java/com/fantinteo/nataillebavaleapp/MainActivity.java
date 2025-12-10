// Le package devrait déjà être le bonpackage com.fantinteo.nataillebavaleapp;

// Imports nécessaires
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.GridLayout;
import android.widget.TextView;
import android.widget.Toast;

// Import de vos classes de modèle
import com.fantinteo.nataillebavaleapp.model.Coordonnee;
import com.fantinteo.nataillebavaleapp.model.ElementNavire;
import com.fantinteo.nataillebavaleapp.model.GrilleDeJeu;
import com.fantinteo.nataillebavaleapp.model.Navire;
import com.fantinteo.nataillebavaleapp.model.TypeNavire;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private GrilleDeJeu grilleDeJeu;
    private GridLayout grilleLayout;

    @java.lang.Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 1. Initialiser la logique du jeu
        grilleDeJeu = new GrilleDeJeu(10, 10);

        // 2. Récupérer le GridLayout depuis le layout XML
        grilleLayout = findViewById(R.id.grille_layout);

        // 3. Placer quelques navires pour le test
        placerNaviresDeTest();

        // 4. Dessiner l'état actuel de la grille
        dessinerGrille();
    }

    private void placerNaviresDeTest() {
        // Crée un croiseur horizontal aux coordonnées (0,0), (1,0), (2,0)
        java.util.List<ElementNavire> elementsCroiseur = new ArrayList<>();
        elementsCroiseur.add(new ElementNavire(new Coordonnee(0, 0)));
        elementsCroiseur.add(new ElementNavire(new Coordonnee(1, 0)));
        elementsCroiseur.add(new ElementNavire(new Coordonnee(2, 0)));
        Navire croiseur = new Navire(TypeNavire.CROISEUR, elementsCroiseur);

        // Crée un escorteur vertical aux coordonnées (5,5), (5,6)
        java.util.List<ElementNavire> elementsEscorteur = new ArrayList<>();
        elementsEscorteur.add(new ElementNavire(new Coordonnee(5, 5)));
        elementsEscorteur.add(new ElementNavire(new Coordonnee(5, 6)));
        Navire escorteur = new Navire(TypeNavire.ESCORTEUR, elementsEscorteur);

        // Tente de placer les navires
        grilleDeJeu.placerNavire(croiseur);
        grilleDeJeu.placerNavire(escorteur);

        // Affiche un message pour confirmer
        Toast.makeText(this, grilleDeJeu.getNavires().size() + " navires placés !", Toast.LENGTH_SHORT).show();
    }

    private void dessinerGrille() {
        // Vide la grille avant de la redessiner
        grilleLayout.removeAllViews();

        int largeurCase = 80; // Largeur de chaque case en pixels
        int hauteurCase = 80; // Hauteur de chaque case en pixels

        // Crée une grille visuelle de 10x10 TextViews
        for (int y = 0; y < grilleDeJeu.getHauteur(); y++) {
            for (int x = 0; x < grilleDeJeu.getLargeur(); x++) {
                TextView caseView = new TextView(this);

                // Personnalisation de la case
                caseView.setWidth(largeurCase);
                caseView.setHeight(hauteurCase);
                caseView.setBackgroundResource(R.drawable.case_border); // On va créer ce drawable juste après
                caseView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

                // Vérifie si un navire se trouve sur cette case
                if (navireSurCase(x, y)) {
                    caseView.setBackgroundColor(0xFF888888); // Couleur gris foncé pour un navire
                    caseView.setText("N");
                } else {
                    caseView.setBackgroundColor(0xFFFFFFFF); // Couleur blanche pour l'eau
                }

                // Ajoute la case au GridLayout
                grilleLayout.addView(caseView);
            }
        }
    }

    // Petite fonction pour vérifier si une case contient une partie de navire
    private boolean navireSurCase(int x, int y) {
        for (Navire navire : grilleDeJeu.getNavires()) {
            for (ElementNavire element : navire.getElements()) {
                if (element.getCoordonnee().getX() == x && element.getCoordonnee().getY() == y) {
                    return true;
                }
            }
        }
        return false;
    }
}
