package com.fantinteo.nataillebavaleapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;
import androidx.core.content.ContextCompat;

import com.fantinteo.nataillebavaleapp.R;
import com.fantinteo.nataillebavaleapp.model.Grid;

public class GridAdapter extends BaseAdapter {
    private final Context context;
    private final Grid playerGrid;
    private final int gridSize;
    private final boolean showShips;

    public GridAdapter(Context context, Grid playerGrid, boolean showShips) {
        this.context = context;
        this.playerGrid = playerGrid;
        this.gridSize = playerGrid.getRows() * playerGrid.getCols();
        this.showShips = showShips;
    }

    @Override
    public int getCount() {
        return gridSize;
    }

    @Override
    public Object getItem(int position) {
        int x = position % playerGrid.getCols();
        int y = position / playerGrid.getCols();
        return new int[]{x, y};
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TextView cell;

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.grid_cell_layout, parent, false);
            cell = convertView.findViewById(R.id.grid_cell_text);
            convertView.setTag(cell);
        } else {
            cell = (TextView) convertView.getTag();
        }

        // On vérifie si la GridView a été mesurée.
        if (parent.getWidth() > 0) {
            GridView gridView = (GridView) parent;
            int numColumns = gridView.getNumColumns();
            // getHorizontalSpacing() est sûr à utiliser ici.
            int horizontalSpacing = gridView.getHorizontalSpacing();

            // Calcul de la largeur exacte d'une colonne.
            int columnWidth = (parent.getWidth() - (horizontalSpacing * (numColumns - 1))) / numColumns;

            // Si la hauteur actuelle de la cellule ne correspond pas, on la force.
            // Cela corrige à la fois le problème de taille initiale et le bug de la cellule bleue.
            if (convertView.getLayoutParams().height != columnWidth) {
                convertView.setLayoutParams(new GridView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, columnWidth));
            }
        }


        int x = position % playerGrid.getCols();
        int y = position / playerGrid.getCols();

        int state = playerGrid.getGridState(x, y);
        int colorRes;

        cell.setText(""); // Réinitialiser le texte

        if (state == 1) { // Navire non touché
            if (showShips) {
                colorRes = R.color.colorShip;
            } else {
                colorRes = R.color.colorWater;
            }
        } else {
            switch (state) {
                case 2: // Touché
                    colorRes = R.color.colorHit;
                    cell.setText("X");
                    break;
                case 3: // Raté
                    colorRes = R.color.colorMiss;
                    cell.setText("O");
                    break;
                case 4: // Coulé
                    colorRes = R.color.colorSunk;
                    cell.setText("C");
                    break;
                default: // Eau
                    colorRes = R.color.colorWater;
                    break;
            }
        }

        cell.setBackgroundColor(ContextCompat.getColor(context, colorRes));

        return convertView;
    }
}
